#!/usr/bin/env python3
"""Generates the Galtzagorri mascot's VectorDrawables, one per pose, from one SVG template.

    python3 tools/design/mascot_to_vector.py

The template (tools/design/art/galtzagorri.svg.template) holds the drawing; ``{{…}}`` holes say
which parts each pose shows and where the eyes and brows go. For every pose the script writes:

* ``docs/art/galtzagorri-<pose>.svg``: the resolved SVG, for review and for the animation tools;
* ``core/designsystem/src/main/res/drawable/core_designsystem_mascot_<pose>.xml``: the vector.

Only the SVG features the template uses are supported: path, circle, ellipse and rect; fill,
stroke, stroke width, caps, joins and opacity; and the transforms ``rotate(a cx cy)`` and
``translate(x y) [scale(-1 1)] rotate(a)``.
"""
from __future__ import annotations

import re
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TEMPLATE = ROOT / "tools/design/art/galtzagorri.svg.template"
SVG_OUT = ROOT / "docs/art"
VECTOR_OUT = ROOT / "core/designsystem/src/main/res/drawable"
ANDROID = "http://schemas.android.com/apk/res/android"
SVG_NS = "{http://www.w3.org/2000/svg}"

POSES = ("hola", "bien", "ups", "piensa")
BROWS = {"hola": (115, 8, 115, 8), "bien": (112, 10, 112, 10), "ups": (116, -16, 116, -16), "piensa": (116, 3, 111, -12)}


def values(pose: str) -> dict[str, str]:
    """The same values the design canvas computes for each pose."""
    def show(on: bool) -> str:
        return "inline" if on else "none"

    up = pose == "piensa"
    y_left, a_left, y_right, a_right = BROWS[pose]
    v = {
        "e.lx": "103", "e.rx": "137", "e.y": "122" if up else "125",
        "e.lgx": "104" if up else "104.6", "e.rgx": "138" if up else "138.6", "e.gy": "119.5" if up else "123",
        "b.l": f"translate(103 {y_left}) rotate({a_left})",
        "b.r": f"translate(137 {y_right}) scale(-1 1) rotate({a_right})",
        "d.sparkles": show(pose == "bien"),
        "d.makilaDown": show(pose != "bien"),
        "d.makilaUp": show(pose == "bien"),
        "d.armLeftDown": show(pose != "bien"),
        "d.armLeftUp": show(pose == "bien"),
        "d.armRightWave": show(pose == "hola"),
        "d.armRightUp": show(pose == "bien"),
        "d.handHead": show(pose == "ups"),
        "d.handBeard": show(pose == "piensa"),
        "d.eyesDot": show(pose != "bien"),
        "d.eyesHappy": show(pose == "bien"),
        "d.mouthHola": show(pose == "hola"),
        "d.mouthBien": show(pose == "bien"),
        "d.mouthUps": show(pose == "ups"),
        "d.mouthPiensa": show(pose == "piensa"),
    }
    return v


def resolve(template: str, pose: str) -> str:
    v = values(pose)
    body = re.sub(r"<!--.*?-->\n?", "", template, flags=re.S)
    body = re.sub(r"\{\{\s*([\w.]+)\s*\}\}", lambda m: v[m.group(1)], body)
    return body.replace("<svg ", '<svg xmlns="http://www.w3.org/2000/svg" ', 1)


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
    """Android group attributes for the template's transforms (None when there is none)."""
    if not transform:
        return None
    m = re.fullmatch(r"rotate\(([-\d.]+)\s+([-\d.]+)\s+([-\d.]+)\)", transform.strip())
    if m:
        a, cx, cy = m.groups()
        return {"rotation": a, "pivotX": cx, "pivotY": cy}
    m = re.fullmatch(r"translate\(([-\d.]+)\s+([-\d.]+)\)(\s+scale\(-1 1\))?\s+rotate\(([-\d.]+)\)", transform.strip())
    if m:
        x, y, mirror, a = m.groups()
        # SVG applies translate * scale * rotate; an Android group applies translate * rotate * scale.
        # Mirroring flips the sense of the rotation, so the angle changes sign.
        attrs = {"translateX": x, "translateY": y, "rotation": fmt(-float(a)) if mirror else a}
        if mirror:
            attrs["scaleX"] = "-1"
        return attrs
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


def convert(el: ET.Element, parent: ET.Element, opacity: float = 1.0) -> None:
    if el.get("display") == "none":
        return
    tag = el.tag.removeprefix(SVG_NS)
    alpha = opacity * num(el.get("opacity"), 1.0)
    if tag in ("defs", "title"):
        return
    if tag == "g":
        attrs = group_for(el.get("transform", ""))
        target = ET.SubElement(parent, "group", {f"{{{ANDROID}}}{k}": v for k, v in attrs.items()}) if attrs else parent
        for child in el:
            convert(child, target, alpha)
        return
    attrs = group_for(el.get("transform", ""))
    target = ET.SubElement(parent, "group", {f"{{{ANDROID}}}{k}": v for k, v in attrs.items()}) if attrs else parent
    a = {f"{{{ANDROID}}}pathData": path_data(el)}
    # A stroked path without a fill is a line in this drawing: give it no fill.
    fill = el.get("fill", "none" if el.get("stroke") else "#000000")
    if fill != "none":
        a[f"{{{ANDROID}}}fillColor"] = color(fill)
        if alpha < 1:
            a[f"{{{ANDROID}}}fillAlpha"] = fmt(alpha)
    stroke = el.get("stroke")
    if stroke and stroke != "none":
        a[f"{{{ANDROID}}}strokeColor"] = color(stroke)
        a[f"{{{ANDROID}}}strokeWidth"] = el.get("stroke-width", "1")
        if alpha < 1:
            a[f"{{{ANDROID}}}strokeAlpha"] = fmt(alpha)
        if el.get("stroke-linecap"):
            a[f"{{{ANDROID}}}strokeLineCap"] = el.get("stroke-linecap")
        if el.get("stroke-linejoin"):
            a[f"{{{ANDROID}}}strokeLineJoin"] = el.get("stroke-linejoin")
    ET.SubElement(target, "path", a)


def vector(svg_text: str, pose: str) -> str:
    root = ET.fromstring(svg_text)
    ET.register_namespace("android", ANDROID)
    out = ET.Element("vector", {
        f"{{{ANDROID}}}width": "120dp", f"{{{ANDROID}}}height": "140dp",
        f"{{{ANDROID}}}viewportWidth": "240", f"{{{ANDROID}}}viewportHeight": "280",
    })
    for child in root:
        convert(child, out)
    ET.indent(out, space="    ")
    header = (f'<?xml version="1.0" encoding="utf-8"?>\n'
              f"<!-- GENERATED by tools/design/mascot_to_vector.py ({pose}). Do not edit by hand. -->\n")
    return header + ET.tostring(out, encoding="unicode") + "\n"


def main() -> None:
    template = TEMPLATE.read_text(encoding="utf-8")
    SVG_OUT.mkdir(parents=True, exist_ok=True)
    for pose in POSES:
        svg = resolve(template, pose)
        (SVG_OUT / f"galtzagorri-{pose}.svg").write_text(svg + "\n", encoding="utf-8")
        target = VECTOR_OUT / f"core_designsystem_mascot_{pose}.xml"
        target.write_text(vector(svg, pose), encoding="utf-8")
        print(f"wrote {target.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
