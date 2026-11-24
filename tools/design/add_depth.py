#!/usr/bin/env python3
"""Gives the cast and the scenes their volume: "flat with depth".

    python3 tools/design/add_depth.py            # rewrite tools/design/art/{cast,scenes}/*.svg
    python3 tools/design/add_depth.py --check    # fail if a drawing is missing its depth

Every large filled shape gets, right after it and in the same group (so it moves with it):

* a **shade**: the shape minus a copy of itself moved up and left, in a darker tone of its colour,
  so the light seems to come from the upper left;
* a **rim light**: the shape minus a copy moved slightly down and right, in translucent white.

Both are baked as standard SVG (a ``clipPath`` with the shape and an even-odd path), which
``cast_to_vector.py`` turns into a VectorDrawable ``clip-path`` and ``fillType="evenOdd"``. The
generated groups carry ``data-depth-generated`` and are rebuilt on every run, so the flat shapes
stay the source to edit. A shape can opt out with ``data-depth="none"`` or set its own offset
with ``data-depth="dx dy"``.
"""
from __future__ import annotations

import argparse
import colorsys
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
FOLDERS = [ROOT / "tools/design/art/cast", ROOT / "tools/design/art/scenes"]
SVG = "http://www.w3.org/2000/svg"
NS = f"{{{SVG}}}"
MARK = "data-depth-generated"
SHAPES = ("path", "circle", "ellipse", "rect")
MIN_SIZE = 22.0
INHERITED = ("fill", "stroke")

sys.path.insert(0, str(Path(__file__).resolve().parent))
from cast_to_vector import fmt, path_data  # noqa: E402

# ---------------------------------------------------------------- path geometry
TOKEN = re.compile(r"[MmLlHhVvCcSsQqTtAaZz]|-?(?:\d+\.?\d*|\.\d+)(?:e-?\d+)?")
ARITY = {"M": 2, "L": 2, "H": 1, "V": 1, "C": 6, "S": 4, "Q": 4, "T": 2, "A": 7, "Z": 0}


def commands(d: str):
    """Yields (command, numbers) pairs, splitting implicit repetitions."""
    tokens = TOKEN.findall(d)
    i, cmd = 0, None
    while i < len(tokens):
        if tokens[i].isalpha():
            cmd = tokens[i]
            i += 1
            if cmd in "Zz":
                yield cmd, []
                continue
        n = ARITY[cmd.upper()]
        yield cmd, [float(t) for t in tokens[i:i + n]]
        i += n
        if cmd == "M":
            cmd = "L"
        elif cmd == "m":
            cmd = "l"


def translate(d: str, dx: float, dy: float) -> str:
    """The path moved by (dx, dy): absolute coordinates move, relative ones do not."""
    out = []
    first = True
    for cmd, args in commands(d):
        a = list(args)
        upper = cmd.upper()
        absolute = cmd.isupper() or (first and cmd == "m")
        if absolute and upper != "Z":
            if upper == "H":
                a[0] += dx
            elif upper == "V":
                a[0] += dy
            elif upper == "A":
                a[5] += dx
                a[6] += dy
            else:
                for k in range(0, len(a), 2):
                    a[k] += dx
                    a[k + 1] += dy
        first = False
        out.append(cmd + " ".join(fmt(v) for v in a))
    return " ".join(out)


def bounds(d: str) -> tuple[float, float, float, float]:
    """A box around every point and control point (enough to tell big shapes from details)."""
    x = y = 0.0
    start = (0.0, 0.0)
    xs: list[float] = []
    ys: list[float] = []
    for cmd, a in commands(d):
        upper, rel = cmd.upper(), cmd.islower()
        if upper == "Z":
            x, y = start
            continue
        if upper == "H":
            x = x + a[0] if rel else a[0]
        elif upper == "V":
            y = y + a[0] if rel else a[0]
        elif upper == "A":
            ex, ey = (x + a[5], y + a[6]) if rel else (a[5], a[6])
            xs += [x - a[0], x + a[0], ex - a[0], ex + a[0]]
            ys += [y - a[1], y + a[1], ey - a[1], ey + a[1]]
            x, y = ex, ey
        else:
            pts = [(a[k], a[k + 1]) for k in range(0, len(a), 2)]
            pts = [(x + px, y + py) for px, py in pts] if rel else pts
            xs += [p[0] for p in pts]
            ys += [p[1] for p in pts]
            x, y = pts[-1]
        if upper == "M":
            start = (x, y)
        xs.append(x)
        ys.append(y)
    return min(xs), min(ys), max(xs), max(ys)


# ---------------------------------------------------------------- colour
def rgb(hex_color: str) -> tuple[float, float, float]:
    h = hex_color.lstrip("#")
    return tuple(int(h[i:i + 2], 16) / 255 for i in (0, 2, 4))  # type: ignore[return-value]


def hex_of(r: float, g: float, b: float) -> str:
    return "#" + "".join(f"{round(max(0, min(1, c)) * 255):02X}" for c in (r, g, b))


SHADOW = rgb("#2A1A12")


def shade_of(fill: str) -> str:
    """The colour in shadow: mixed with a warm dark brown, a little more for darker colours."""
    t = 0.16 + 0.14 * (1 - light(fill))
    return hex_of(*(c + (s - c) * t for c, s in zip(rgb(fill), SHADOW)))


def light(fill: str) -> float:
    return colorsys.rgb_to_hls(*rgb(fill))[1]


# ---------------------------------------------------------------- the pass
def strip(root: ET.Element) -> None:
    """Removes the depth groups and clip paths a previous run added."""
    for parent in root.iter():
        for child in list(parent):
            if child.get(MARK) is not None:
                parent.remove(child)


def deepen(root: ET.Element, prefix: str) -> int:
    count = 0

    def walk(parent: ET.Element, inherited: dict[str, str]) -> None:
        nonlocal count
        style = {**inherited, **{k: parent.get(k) for k in INHERITED if parent.get(k) is not None}}
        for index, el in reversed(list(enumerate(list(parent)))):
            tag = el.tag.removeprefix(NS)
            if tag == "g":
                walk(el, style)
                continue
            if tag not in SHAPES or el.get("data-depth") == "none":
                continue
            fill = el.get("fill", style.get("fill"))
            if not fill or not fill.startswith("#") or len(fill) != 7:
                continue
            if el.get("opacity") or el.get("fill-opacity") or light(fill) > 0.9:
                continue
            d = path_data(el)
            x0, y0, x1, y1 = bounds(d)
            w, h = x1 - x0, y1 - y0
            if min(w, h) < MIN_SIZE:
                continue
            if el.get("data-depth"):
                dx, dy = (float(v) for v in el.get("data-depth").split())
            else:
                k = max(4.0, min(10.0, 0.12 * min(w, h)))
                dx, dy = -k, -k * 0.9
            count += 1
            clip_id = f"{prefix}-{count}"
            group = ET.Element(f"{NS}g", {MARK: ""})
            if el.get("transform"):
                group.set("transform", el.get("transform"))
            clip = ET.SubElement(group, f"{NS}clipPath", {"id": clip_id})
            ET.SubElement(clip, f"{NS}path", {"d": d})
            inner = ET.SubElement(group, f"{NS}g", {"clip-path": f"url(#{clip_id})"})
            frame = f"M{fmt(x0 - 40)} {fmt(y0 - 40)} H{fmt(x1 + 40)} V{fmt(y1 + 40)} H{fmt(x0 - 40)} Z"
            ET.SubElement(inner, f"{NS}path", {
                "d": f"{frame} {translate(d, dx, dy)}", "fill-rule": "evenodd", "fill": shade_of(fill),
            })
            ET.SubElement(inner, f"{NS}path", {
                "d": f"{frame} {translate(d, 2, 3)}", "fill-rule": "evenodd", "fill": "#FFFFFF",
                "opacity": "0.14",
            })
            parent.insert(index + 1, group)

    walk(root, {})
    return count


def process(path: Path) -> str:
    ET.register_namespace("", SVG)
    source = path.read_text(encoding="utf-8")
    # The comment above the drawing explains it; keep it as it is.
    head = source[: source.index("<svg")]
    parser = ET.XMLParser(target=ET.TreeBuilder(insert_comments=True))
    root = ET.fromstring(source, parser=parser)
    strip(root)
    deepen(root, path.stem)
    ET.indent(root, space="  ")
    return head + ET.tostring(root, encoding="unicode") + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    stale = []
    for folder in FOLDERS:
        for svg in sorted(folder.glob("*.svg")):
            text = process(svg)
            if svg.read_text(encoding="utf-8") != text:
                stale.append(svg)
                if not args.check:
                    svg.write_text(text, encoding="utf-8")
    if args.check and stale:
        for s in stale:
            print(f"missing depth: {s.relative_to(ROOT)}", file=sys.stderr)
        return 1
    print(f"{'checked' if args.check else 'updated'}: {len(stale)} drawing(s) changed")
    return 0


if __name__ == "__main__":
    sys.exit(main())
