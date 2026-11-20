#!/usr/bin/env python3
"""Generates the cast's VectorDrawables and the Kotlin that stacks and animates them.

    python3 tools/design/cast_to_vector.py           # write
    python3 tools/design/cast_to_vector.py --check   # fail if anything is out of date

The cast (Ane, Mikel, Maite and Unai) is drawn in ``tools/design/art/cast/<member>-<pose>.svg``,
one 240 x 280 SVG per member and pose. A group with ``data-motion="Name"`` moves on its own:
``Name`` is a ``Motion`` in ``CastMotion.kt`` and ``data-pivot="x y"`` the point it turns or
scales around, in the drawing's coordinates. Animated groups may nest (a head that tilts inside
a body that jumps).

For every drawing the script writes:

* ``core_designsystem_cast_<member>_<pose>.xml``: the whole drawing, shown when motion is off;
* ``core_designsystem_cast_<member>_<pose>_<n>.xml``: one vector per layer. Runs of still shapes
  are merged into one layer; every animated group without animated groups inside is one layer.
  The layers share the full canvas, so stacked in order they draw the same picture;
* ``CastArt.kt``: for each member and pose, the tree of layers with their motions and pivots.

Only the SVG features the drawings use are supported: path, circle, ellipse and rect; fill,
stroke, stroke width, caps, joins, opacity and fill-opacity; and ``rotate(a cx cy)`` transforms.
"""
from __future__ import annotations

import argparse
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
ART = ROOT / "tools/design/art/cast"
VECTOR_OUT = ROOT / "core/designsystem/src/main/res/drawable"
KOTLIN_OUT = ROOT / "core/designsystem/src/main/kotlin/eus/hitz/core/designsystem/component/CastArt.kt"
MOTIONS = ROOT / "core/designsystem/src/main/kotlin/eus/hitz/core/designsystem/component/CastMotion.kt"
ANDROID = "http://schemas.android.com/apk/res/android"
SVG_NS = "{http://www.w3.org/2000/svg}"
PREFIX = "core_designsystem_cast_"

MEMBERS = ("ane", "mikel", "maite", "unai")
POSES = ("hola", "bien", "ups", "piensa")


# ---------------------------------------------------------------- SVG → VectorDrawable
def num(x: str | None, default: float = 0.0) -> float:
    return float(x) if x is not None else default


def fmt(x: float) -> str:
    return f"{x:.2f}".rstrip("0").rstrip(".")


def ellipse_path(cx: float, cy: float, rx: float, ry: float) -> str:
    return (f"M{fmt(cx - rx)},{fmt(cy)} a{fmt(rx)},{fmt(ry)} 0 1,0 {fmt(2 * rx)},0 "
            f"a{fmt(rx)},{fmt(ry)} 0 1,0 {fmt(-2 * rx)},0 Z")


def rect_path(x: float, y: float, w: float, h: float, r: float) -> str:
    if r <= 0:
        return f"M{fmt(x)},{fmt(y)} h{fmt(w)} v{fmt(h)} h{fmt(-w)} Z"
    r = min(r, w / 2, h / 2)
    return (f"M{fmt(x + r)},{fmt(y)} h{fmt(w - 2 * r)} a{fmt(r)},{fmt(r)} 0 0,1 {fmt(r)},{fmt(r)} "
            f"v{fmt(h - 2 * r)} a{fmt(r)},{fmt(r)} 0 0,1 {fmt(-r)},{fmt(r)} h{fmt(-(w - 2 * r))} "
            f"a{fmt(r)},{fmt(r)} 0 0,1 {fmt(-r)},{fmt(-r)} v{fmt(-(h - 2 * r))} "
            f"a{fmt(r)},{fmt(r)} 0 0,1 {fmt(r)},{fmt(-r)} Z")


def color(value: str) -> str:
    return "#FF" + value.lstrip("#").upper()


def group_for(transform: str) -> dict[str, str] | None:
    """Android group attributes for an SVG transform (None when there is none)."""
    if not transform:
        return None
    m = re.fullmatch(r"rotate\(([-\d.]+)\s+([-\d.]+)\s+([-\d.]+)\)", transform.strip())
    if m:
        a, cx, cy = m.groups()
        return {"rotation": a, "pivotX": cx, "pivotY": cy}
    raise ValueError(f"unsupported transform: {transform}")


def path_data(el: ET.Element) -> str:
    tag = el.tag.removeprefix(SVG_NS)
    if tag == "path":
        return el.get("d", "")
    if tag == "circle":
        r = num(el.get("r"))
        return ellipse_path(num(el.get("cx")), num(el.get("cy")), r, r)
    if tag == "ellipse":
        return ellipse_path(num(el.get("cx")), num(el.get("cy")), num(el.get("rx")), num(el.get("ry")))
    if tag == "rect":
        return rect_path(num(el.get("x")), num(el.get("y")), num(el.get("width")), num(el.get("height")),
                         num(el.get("rx")))
    raise ValueError(f"unsupported element: {tag}")


INHERITED = ("fill", "stroke", "stroke-width", "stroke-linecap", "stroke-linejoin")


def convert(el: ET.Element, parent: ET.Element, opacity: float = 1.0, inherited: dict[str, str] | None = None) -> None:
    tag = el.tag.removeprefix(SVG_NS)
    # Presentation attributes set on a group apply to its children unless they set their own.
    style = {**(inherited or {}), **{k: el.get(k) for k in INHERITED if el.get(k) is not None}}
    alpha = opacity * num(el.get("opacity"), 1.0)
    if tag in ("defs", "title", "style"):
        return
    attrs = group_for(el.get("transform", ""))
    target = ET.SubElement(parent, "group", {f"{{{ANDROID}}}{k}": v for k, v in attrs.items()}) if attrs else parent
    if tag == "g":
        for child in el:
            convert(child, target, alpha, style)
        return
    a = {f"{{{ANDROID}}}pathData": path_data(el)}
    # A stroked path without a fill is a line in these drawings: give it no fill.
    fill = style.get("fill", "none" if style.get("stroke") else "#000000")
    if fill != "none":
        a[f"{{{ANDROID}}}fillColor"] = color(fill)
        fill_alpha = alpha * num(el.get("fill-opacity"), 1.0)
        if fill_alpha < 1:
            a[f"{{{ANDROID}}}fillAlpha"] = fmt(fill_alpha)
    stroke = style.get("stroke")
    if stroke and stroke != "none":
        a[f"{{{ANDROID}}}strokeColor"] = color(stroke)
        a[f"{{{ANDROID}}}strokeWidth"] = style.get("stroke-width", "1")
        if alpha < 1:
            a[f"{{{ANDROID}}}strokeAlpha"] = fmt(alpha)
        if style.get("stroke-linecap"):
            a[f"{{{ANDROID}}}strokeLineCap"] = style["stroke-linecap"]
        if style.get("stroke-linejoin"):
            a[f"{{{ANDROID}}}strokeLineJoin"] = style["stroke-linejoin"]
    ET.SubElement(target, "path", a)


def vector(elements: list[ET.Element], label: str) -> str:
    ET.register_namespace("android", ANDROID)
    out = ET.Element("vector", {
        f"{{{ANDROID}}}width": "120dp", f"{{{ANDROID}}}height": "140dp",
        f"{{{ANDROID}}}viewportWidth": "240", f"{{{ANDROID}}}viewportHeight": "280",
    })
    for el in elements:
        convert(el, out)
    ET.indent(out, space="    ")
    header = (f'<?xml version="1.0" encoding="utf-8"?>\n'
              f"<!-- GENERATED by tools/design/cast_to_vector.py ({label}). Do not edit by hand. -->\n")
    return header + ET.tostring(out, encoding="unicode") + "\n"


# ---------------------------------------------------------------- layer tree
class Layer:
    def __init__(self, elements: list[ET.Element], motion: str | None = None, pivot: tuple[float, float] = (0, 0)):
        self.elements, self.motion, self.pivot = elements, motion, pivot
        self.name = ""


class Group:
    def __init__(self, motion: str, pivot: tuple[float, float], children: list):
        self.motion, self.pivot, self.children = motion, pivot, children


def animated_inside(el: ET.Element) -> bool:
    return any(d.get("data-motion") for d in el.iter() if d is not el)


def pivot_of(el: ET.Element) -> tuple[float, float]:
    x, y = el.get("data-pivot", "120 140").split()
    return float(x), float(y)


def tree(container: ET.Element) -> list:
    nodes: list = []
    still: list[ET.Element] = []

    def flush() -> None:
        if still:
            nodes.append(Layer(list(still)))
            still.clear()

    for el in container:
        motion = el.get("data-motion")
        if animated_inside(el) and any(el.get(a) is not None for a in ("transform", "opacity", *INHERITED)):
            raise ValueError("a group split into layers cannot carry a transform, an opacity or paint attributes")
        if motion:
            flush()
            if animated_inside(el):
                nodes.append(Group(motion, pivot_of(el), tree(el)))
            else:
                nodes.append(Layer([el], motion, pivot_of(el)))
        elif animated_inside(el):
            flush()
            nodes.extend(tree(el))
        else:
            still.append(el)
    flush()
    return merge_still(nodes)


def merge_still(nodes: list) -> list:
    """Joins neighbouring still layers into one, so the app stacks as few images as possible."""
    out: list = []
    for n in nodes:
        if isinstance(n, Layer) and n.motion is None and out and isinstance(out[-1], Layer) and out[-1].motion is None:
            out[-1].elements.extend(n.elements)
        else:
            out.append(n)
    return out


def layers(nodes: list) -> list[Layer]:
    out: list[Layer] = []
    for n in nodes:
        out.extend(layers(n.children) if isinstance(n, Group) else [n])
    return out


# ---------------------------------------------------------------- Kotlin
def kotlin_nodes(nodes: list, indent: int) -> list[str]:
    pad = " " * indent
    lines: list[str] = []
    for n in nodes:
        if isinstance(n, Layer):
            if n.motion:
                lines.append(f"{pad}CastLayer(R.drawable.{n.name}, Motion.{n.motion}, "
                             f"{fmt(n.pivot[0])}f, {fmt(n.pivot[1])}f),")
            else:
                lines.append(f"{pad}CastLayer(R.drawable.{n.name}),")
        else:
            lines.append(f"{pad}CastGroup(")
            lines.append(f"{pad}    Motion.{n.motion},")
            lines.append(f"{pad}    {fmt(n.pivot[0])}f,")
            lines.append(f"{pad}    {fmt(n.pivot[1])}f,")
            lines.append(f"{pad}    listOf(")
            lines.extend(kotlin_nodes(n.children, indent + 8))
            lines.append(f"{pad}    ),")
            lines.append(f"{pad}),")
    return lines


def kotlin(art: dict[tuple[str, str], list]) -> str:
    lines = [
        "// GENERATED by tools/design/cast_to_vector.py from tools/design/art/cast/. Do not edit by hand.",
        "package eus.hitz.core.designsystem.component",
        "",
        "import eus.hitz.core.designsystem.R",
        "",
        "/** Every member's drawings, by pose: the still drawable and the animated layer tree. */",
        "internal val CastArtwork: Map<CastMember, Map<MascotPose, CastArt>> = mapOf(",
    ]
    for member in MEMBERS:
        lines.append(f"    CastMember.{member.capitalize()} to mapOf(")
        for pose in POSES:
            lines.append(f"        MascotPose.{pose.capitalize()} to CastArt(")
            lines.append(f"            still = R.drawable.{PREFIX}{member}_{pose},")
            lines.append("            nodes = listOf(")
            lines.extend(kotlin_nodes(art[(member, pose)], 16))
            lines.append("            ),")
            lines.append("        ),")
        lines.append("    ),")
    lines.append(")")
    return "\n".join(lines) + "\n"


def known_motions() -> set[str]:
    text = MOTIONS.read_text(encoding="utf-8")
    body = text.split("enum class Motion", 1)[1]
    return set(re.findall(r"^    ([A-Z]\w*)\(", body, flags=re.M))


# ---------------------------------------------------------------- main
def outputs() -> dict[Path, str]:
    files: dict[Path, str] = {}
    art: dict[tuple[str, str], list] = {}
    motions = known_motions()
    for member in MEMBERS:
        for pose in POSES:
            source = ART / f"{member}-{pose}.svg"
            root = ET.fromstring(source.read_text(encoding="utf-8"))
            for el in root.iter():
                m = el.get("data-motion")
                if m and m not in motions:
                    raise ValueError(f"{source.name}: unknown motion {m} (add it to CastMotion.kt)")
            nodes = tree(root)
            for i, layer in enumerate(layers(nodes), start=1):
                layer.name = f"{PREFIX}{member}_{pose}_{i}"
                files[VECTOR_OUT / f"{layer.name}.xml"] = vector(layer.elements, f"{member} {pose}, layer {i}")
            files[VECTOR_OUT / f"{PREFIX}{member}_{pose}.xml"] = vector(list(root), f"{member} {pose}")
            art[(member, pose)] = nodes
    files[KOTLIN_OUT] = kotlin(art)
    return files


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    parser.add_argument("--check", action="store_true", help="fail if the generated files are out of date")
    args = parser.parse_args()
    files = outputs()
    stale = sorted(p for p in VECTOR_OUT.glob(f"{PREFIX}*.xml") if p not in files)
    if args.check:
        bad = [p for p, text in files.items() if not p.exists() or p.read_text(encoding="utf-8") != text]
        for p in bad + stale:
            print(f"out of date: {p.relative_to(ROOT)}", file=sys.stderr)
        if bad or stale:
            print("run: python3 tools/design/cast_to_vector.py", file=sys.stderr)
            return 1
        print(f"{len(files)} generated files up to date")
        return 0
    for p in stale:
        p.unlink()
    for p, text in files.items():
        p.write_text(text, encoding="utf-8")
    print(f"wrote {len(files)} files")
    return 0


if __name__ == "__main__":
    sys.exit(main())
