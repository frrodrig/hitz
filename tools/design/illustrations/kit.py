"""Tiny SVG kit for the course illustrations.

Every picture is drawn on a 100 × 100 canvas inside a rounded square, in a flat style with no
outlines: a soft background, one or two big shapes and a darker shade for depth. Helpers return SVG
fragments as strings, so a drawing is a list of calls.
"""
from __future__ import annotations

from collections.abc import Callable, Iterable

# Palette shared by every drawing. Hues follow the app's theme (red, teal, navy) plus natural colors.
RED = "#C8102E"
RED_DARK = "#930B22"
TEAL = "#5BA8B5"
TEAL_DARK = "#3D7F8B"
TEAL_LIGHT = "#C9E4E8"
NAVY = "#1E2A44"
NAVY_LIGHT = "#33476E"
SKIN = "#F2C49B"
SKIN_SHADE = "#DCA57A"
SKIN_DARK = "#B97A50"
SKIN_DARK_SHADE = "#9A6140"
HAIR_BROWN = "#5B3A29"
HAIR_BLACK = "#2B2522"
HAIR_BLOND = "#E0B45A"
HAIR_GREY = "#C9CCD1"
WHITE = "#FFFFFF"
OFF_WHITE = "#F6F3EE"
GREY = "#9AA0A8"
GREY_LIGHT = "#DDE1E6"
GREY_DARK = "#5E646C"
BLACK = "#2A2A2E"
YELLOW = "#F6C431"
YELLOW_DARK = "#D9A21B"
ORANGE = "#F39A2B"
ORANGE_DARK = "#D57A12"
GREEN = "#5DA66B"
GREEN_DARK = "#3E7F4D"
GREEN_LIGHT = "#A8D59A"
BROWN = "#9A6B43"
BROWN_DARK = "#6E4A2C"
BROWN_LIGHT = "#C99A6A"
BLUE = "#3C7DD9"
BLUE_DARK = "#285BA6"
SKY = "#CFE8F5"
PINK = "#F3A6B5"
PURPLE = "#8D6CC4"
CREAM = "#FFE9C7"

# Soft backgrounds, one per family of pictures.
BG = {
    "people": "#FCE3DF",
    "place": "#D6ECF3",
    "home": "#F4E7D7",
    "food": "#FFF1D6",
    "drink": "#E3F1E8",
    "kitchen": "#E8ECF4",
    "clothes": "#EEE4F4",
    "color": "#E6E2DC",
    "body": "#FBE6D8",
    "nature": "#DDF0D8",
    "transport": "#E2E9F5",
    "leisure": "#FDE7CF",
    "object": "#ECEFF2",
    "weather": "#D9ECF7",
}


def _attrs(**kw: object) -> str:
    names = {"sw": "stroke-width", "cap": "stroke-linecap", "join": "stroke-linejoin", "op": "opacity",
             "fop": "fill-opacity", "transform": "transform"}
    out = []
    for key, value in kw.items():
        if value is None:
            continue
        out.append(f'{names.get(key, key)}="{value}"')
    return (" " + " ".join(out)) if out else ""


def rect(x: float, y: float, w: float, h: float, fill: str, r: float = 0, **kw: object) -> str:
    return f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{r}" fill="{fill}"{_attrs(**kw)}/>'


def circle(cx: float, cy: float, r: float, fill: str, **kw: object) -> str:
    return f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="{fill}"{_attrs(**kw)}/>'


def ellipse(cx: float, cy: float, rx: float, ry: float, fill: str, **kw: object) -> str:
    return f'<ellipse cx="{cx}" cy="{cy}" rx="{rx}" ry="{ry}" fill="{fill}"{_attrs(**kw)}/>'


def path(d: str, fill: str = "none", **kw: object) -> str:
    return f'<path d="{d}" fill="{fill}"{_attrs(**kw)}/>'


def poly(points: str, fill: str, **kw: object) -> str:
    return f'<polygon points="{points}" fill="{fill}"{_attrs(**kw)}/>'


def line(x1: float, y1: float, x2: float, y2: float, stroke: str, sw: float = 2, **kw: object) -> str:
    kw.setdefault("cap", "round")
    return f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="{stroke}"{_attrs(sw=sw, **kw)}/>'


def stroke(d: str, color: str, sw: float = 2, **kw: object) -> str:
    """An open, round-capped line along a path."""
    return path(d, "none", stroke=color, sw=sw, cap="round", join="round", **kw)


def group(items: Iterable[str], transform: str | None = None, **kw: object) -> str:
    return f'<g{_attrs(transform=transform, **kw)}>{"".join(items)}</g>'


def ground(color: str = GREEN, y: float = 78) -> str:
    """A gentle hill along the bottom, as in the greeting pictures."""
    return path(f"M0 {y} Q 25 {y - 6} 50 {y} T 100 {y} V 100 H 0 Z", color)


def shadow(cx: float = 50, cy: float = 88, rx: float = 28) -> str:
    return ellipse(cx, cy, rx, 3.5, "#000000", op=0.08)


def svg(background: str, items: Iterable[str], size: int = 100) -> str:
    body = "".join(items)
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {size} {size}">'
        f'<defs><clipPath id="c"><rect width="{size}" height="{size}" rx="14"/></clipPath></defs>'
        f'<g clip-path="url(#c)"><rect width="{size}" height="{size}" fill="{background}"/>{body}</g></svg>'
    )


Drawing = Callable[[], list[str]]
REGISTRY: dict[str, tuple[str, Drawing]] = {}


def art(image_id: str, background: str) -> Callable[[Drawing], Drawing]:
    """Registers a drawing under its image id (the asset is ``<id>.webp``)."""
    def register(draw: Drawing) -> Drawing:
        if image_id in REGISTRY:
            raise ValueError(f"duplicated illustration {image_id}")
        REGISTRY[image_id] = (BG.get(background, background), draw)
        return draw
    return register


# --------------------------------------------------------------------------------------------
# People
# --------------------------------------------------------------------------------------------

def face(cx: float, cy: float, r: float, skin: str = SKIN, smile: bool = True, blush: bool = True) -> list[str]:
    eye_dx, eye_y = r * 0.36, cy - r * 0.05
    out = [
        circle(cx, cy, r, skin),
        circle(cx - eye_dx, eye_y, r * 0.1, NAVY),
        circle(cx + eye_dx, eye_y, r * 0.1, NAVY),
    ]
    if blush:
        out += [circle(cx - r * 0.55, cy + r * 0.28, r * 0.13, PINK, op=0.7),
                circle(cx + r * 0.55, cy + r * 0.28, r * 0.13, PINK, op=0.7)]
    if smile:
        out.append(stroke(f"M{cx - r * 0.25} {cy + r * 0.32} Q {cx} {cy + r * 0.52} {cx + r * 0.25} {cy + r * 0.32}",
                          NAVY, r * 0.09))
    return out


def hair(style: str, cx: float, cy: float, r: float, color: str) -> tuple[list[str], list[str]]:
    """Hair drawn behind and in front of the face, for a head of radius r at (cx, cy)."""
    back: list[str] = []
    front: list[str] = []
    if style == "short":
        front.append(path(f"M{cx - r} {cy - r * 0.05} Q {cx - r * 1.05} {cy - r * 1.15} {cx} {cy - r * 1.08} "
                          f"Q {cx + r * 1.05} {cy - r * 1.15} {cx + r} {cy - r * 0.05} "
                          f"Q {cx + r * 0.7} {cy - r * 0.6} {cx + r * 0.1} {cy - r * 0.62} "
                          f"Q {cx - r * 0.6} {cy - r * 0.6} {cx - r} {cy - r * 0.05} Z", color))
    elif style == "long":
        back.append(path(f"M{cx - r * 1.08} {cy} Q {cx - r * 1.15} {cy - r * 1.2} {cx} {cy - r * 1.12} "
                         f"Q {cx + r * 1.15} {cy - r * 1.2} {cx + r * 1.08} {cy} L {cx + r * 1.12} {cy + r * 1.5} "
                         f"Q {cx} {cy + r * 1.7} {cx - r * 1.12} {cy + r * 1.5} Z", color))
        front.append(path(f"M{cx - r * 0.98} {cy + r * 0.1} Q {cx - r * 0.95} {cy - r * 1.05} {cx + r * 0.1} "
                          f"{cy - r * 1.05} Q {cx + r * 1.0} {cy - r * 1.0} {cx + r * 0.98} {cy + r * 0.1} "
                          f"Q {cx + r * 0.6} {cy - r * 0.55} {cx - r * 0.1} {cy - r * 0.6} "
                          f"Q {cx - r * 0.7} {cy - r * 0.4} {cx - r * 0.98} {cy + r * 0.1} Z", color))
    elif style == "pigtails":
        back += [circle(cx - r * 1.12, cy + r * 0.1, r * 0.42, color), circle(cx + r * 1.12, cy + r * 0.1, r * 0.42, color)]
        front += hair("short", cx, cy, r, color)[1]
    elif style == "bun":
        back.append(circle(cx, cy - r * 1.05, r * 0.42, color))
        front.append(path(f"M{cx - r * 1.0} {cy + r * 0.05} Q {cx - r * 1.0} {cy - r * 1.05} {cx} {cy - r * 1.02} "
                          f"Q {cx + r * 1.0} {cy - r * 1.05} {cx + r * 1.0} {cy + r * 0.05} "
                          f"Q {cx + r * 0.8} {cy - r * 0.55} {cx} {cy - r * 0.58} "
                          f"Q {cx - r * 0.8} {cy - r * 0.55} {cx - r * 1.0} {cy + r * 0.05} Z", color))
    elif style == "bald":
        front += [path(f"M{cx - r * 1.0} {cy + r * 0.2} Q {cx - r * 1.08} {cy - r * 0.35} {cx - r * 0.75} {cy - r * 0.6} "
                       f"L {cx - r * 0.7} {cy - r * 0.1} Z", color),
                  path(f"M{cx + r * 1.0} {cy + r * 0.2} Q {cx + r * 1.08} {cy - r * 0.35} {cx + r * 0.75} {cy - r * 0.6} "
                       f"L {cx + r * 0.7} {cy - r * 0.1} Z", color)]
    return back, front


def person(cx: float = 50, top: float = 18, scale: float = 1.0, *, skin: str = SKIN, hair_style: str = "short",
           hair_color: str = HAIR_BROWN, shirt: str = TEAL, shirt_shade: str = TEAL_DARK,
           extras_back: Iterable[str] = (), extras_front: Iterable[str] = (), body_bottom: float = 100,
           smile: bool = True) -> list[str]:
    """A friendly bust: head of radius 16 × scale with its top at ``top``, shoulders to the bottom."""
    r = 16 * scale
    cy = top + r
    back, front = hair(hair_style, cx, cy, r, hair_color)
    shoulders_y = cy + r * 1.15
    body_w = r * 2.6
    body = [
        path(f"M{cx - body_w / 2} {body_bottom} V {shoulders_y + r * 0.6} "
             f"Q {cx - body_w / 2} {shoulders_y} {cx - body_w / 2 + r * 0.7} {shoulders_y} "
             f"H {cx + body_w / 2 - r * 0.7} Q {cx + body_w / 2} {shoulders_y} {cx + body_w / 2} "
             f"{shoulders_y + r * 0.6} V {body_bottom} Z", shirt),
        path(f"M{cx - r * 0.45} {shoulders_y} Q {cx} {shoulders_y + r * 0.55} {cx + r * 0.45} {shoulders_y} Z",
             shirt_shade),
        rect(cx - r * 0.28, cy + r * 0.7, r * 0.56, r * 0.6, skin),
    ]
    return [*back, *extras_back, *body, *face(cx, cy, r, skin, smile=smile), *front, *extras_front]


def figure(cx: float, feet: float, height: float, *, skin: str = SKIN, hair_style: str = "short",
           hair_color: str = HAIR_BROWN, shirt: str = TEAL, legs: str = NAVY, skirt: bool = False) -> list[str]:
    """A small full-body figure standing on ``feet`` with the given height."""
    head = height * 0.16
    body_top = feet - height + head * 2.05
    body_h = height * 0.36
    width = height * 0.26
    leg_top = body_top + body_h
    back, front = hair(hair_style, cx, feet - height + head, head, hair_color)
    out = [*back]
    if skirt:
        out.append(path(f"M{cx - width * 0.5} {leg_top - 1} L {cx - width * 0.75} {leg_top + height * 0.18} "
                        f"H {cx + width * 0.75} L {cx + width * 0.5} {leg_top - 1} Z", shirt))
        out += [rect(cx - width * 0.35, leg_top + height * 0.17, width * 0.22, feet - leg_top - height * 0.17, skin),
                rect(cx + width * 0.13, leg_top + height * 0.17, width * 0.22, feet - leg_top - height * 0.17, skin)]
    else:
        out += [rect(cx - width * 0.45, leg_top - 1, width * 0.4, feet - leg_top + 1, legs, r=width * 0.15),
                rect(cx + width * 0.05, leg_top - 1, width * 0.4, feet - leg_top + 1, legs, r=width * 0.15)]
    out += [
        rect(cx - width * 0.5, body_top, width, body_h, shirt, r=width * 0.3),
        rect(cx - width * 0.78, body_top + 1, width * 0.26, body_h * 0.85, shirt, r=width * 0.13),
        rect(cx + width * 0.52, body_top + 1, width * 0.26, body_h * 0.85, shirt, r=width * 0.13),
        circle(cx - width * 0.65, body_top + body_h * 0.92, width * 0.14, skin),
        circle(cx + width * 0.65, body_top + body_h * 0.92, width * 0.14, skin),
        *face(cx, feet - height + head, head, skin, blush=False),
        *front,
    ]
    return out


# --------------------------------------------------------------------------------------------
# Buildings
# --------------------------------------------------------------------------------------------

def window(x: float, y: float, w: float = 9, h: float = 10, frame: str = WHITE, glass: str = SKY) -> list[str]:
    return [rect(x, y, w, h, frame, r=1.2), rect(x + 1.3, y + 1.3, w - 2.6, h - 2.6, glass, r=0.6),
            line(x + w / 2, y + 1.3, x + w / 2, y + h - 1.3, frame, 1)]


def door(x: float, y: float, w: float, h: float, color: str = BROWN, knob: str = YELLOW) -> list[str]:
    return [path(f"M{x} {y + h} V {y + w / 2} A {w / 2} {w / 2} 0 0 1 {x + w} {y + w / 2} V {y + h} Z", color),
            circle(x + w * 0.78, y + h * 0.6, w * 0.07, knob)]


def storefront(awning: str, awning_light: str, wall: str = OFF_WHITE) -> list[str]:
    """A shop front with a striped awning; the sign shows what it sells."""
    stripes = [rect(14 + i * 9, 34, 9, 10, awning if i % 2 == 0 else awning_light) for i in range(8)]
    scallops = [circle(18.5 + i * 9, 44, 4.5, awning if i % 2 == 0 else awning_light) for i in range(8)]
    return [
        ground(GREEN_LIGHT, 86),
        rect(16, 18, 68, 70, wall),
        rect(22, 20, 56, 12, NAVY, r=2),
        *stripes, *scallops,
        rect(22, 54, 30, 22, SKY, r=1.5), rect(22, 54, 30, 4, WHITE, op=0.5),
        *door(58, 52, 18, 36, awning),
    ]
