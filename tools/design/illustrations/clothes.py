"""Clothes and colors (unit 6.2)."""
from __future__ import annotations

from .kit import (
    BLACK, BLUE, BLUE_DARK, BROWN, BROWN_DARK, GREEN, GREEN_DARK, GREY, GREY_DARK, NAVY, OFF_WHITE, PINK,
    PURPLE, RED, RED_DARK, TEAL, WHITE, YELLOW, YELLOW_DARK, art, circle, line, path, poly, rect, shadow, stroke,
)


def hanger() -> list[str]:
    return [stroke("M50 18 q 0 -6 5 -6 q 5 0 4 5 q -1 3 -4 4 L 50 22 L 22 34 H 78 L 50 22", GREY_DARK, 1.8)]


def top(color: str, shade: str, sleeve: float, collar: list[str], length: float = 86) -> list[str]:
    """A shirt-like garment seen from the front; ``sleeve`` is how far the sleeves reach (y)."""
    return [
        path(f"M36 24 L 16 34 L 10 {sleeve} L 24 {sleeve + 2} L 28 44 V {length} H 72 V 44 L 76 {sleeve + 2} "
             f"L 90 {sleeve} L 84 34 L 64 24 Q 50 32 36 24 Z", color),
        path(f"M64 24 L 84 34 L 90 {sleeve} L 76 {sleeve + 2} L 72 44 V {length} H 62 V 30 Z", shade, op=0.35),
        *collar,
    ]


@art("clothes_shirt", "clothes")
def shirt() -> list[str]:
    collar = [poly("36,24 50,34 44,40", WHITE), poly("64,24 50,34 56,40", WHITE),
              *[circle(50, 44 + i * 10, 1.6, NAVY) for i in range(4)], line(50, 36, 50, 86, "#B9D3F2", 1)]
    return top("#DCEBFB", "#9DB9DB", 74, collar)


@art("clothes_tshirt", "clothes")
def tshirt() -> list[str]:
    collar = [path("M38 25 Q 50 36 62 25", "none", stroke=RED_DARK, sw=2.5), rect(28, 52, 44, 6, WHITE, op=0.85),
              rect(28, 62, 44, 3, WHITE, op=0.6)]
    return top(RED, RED_DARK, 46, collar, length=82)


@art("clothes_jumper", "clothes")
def jumper() -> list[str]:
    collar = [path("M36 24 Q 50 36 64 24 Q 50 30 36 24 Z", GREEN_DARK),
              *[rect(28, y, 44, 3, GREEN_DARK, op=0.5) for y in (52, 60)],
              rect(28, 82, 44, 4, GREEN_DARK), rect(10, 72, 14, 4, GREEN_DARK), rect(76, 72, 14, 4, GREEN_DARK)]
    return top(GREEN, GREEN_DARK, 74, collar)


@art("clothes_coat", "clothes")
def coat() -> list[str]:
    collar = [poly("36,24 50,40 42,46 32,30", BROWN_DARK), poly("64,24 50,40 58,46 68,30", BROWN_DARK),
              line(50, 40, 50, 92, BROWN_DARK, 1.4), *[circle(54, 50 + i * 12, 2, YELLOW_DARK) for i in range(3)],
              rect(32, 66, 12, 3, BROWN_DARK, r=1), rect(56, 66, 12, 3, BROWN_DARK, r=1)]
    return top(BROWN, BROWN_DARK, 80, collar, length=92)


@art("clothes_trousers", "clothes")
def trousers() -> list[str]:
    return [
        shadow(50, 92, 26),
        path("M30 14 H 70 L 76 90 H 56 L 50 40 L 44 90 H 24 Z", BLUE),
        path("M50 40 L 56 90 H 64 L 58 30 Z", BLUE_DARK, op=0.5),
        rect(30, 14, 40, 7, BLUE_DARK), rect(46, 15.5, 8, 4, YELLOW_DARK, r=1),
        stroke("M36 21 q 2 8 10 8 M64 21 q -2 8 -10 8", "#7FA9E8", 1.4),
    ]


@art("clothes_skirt", "clothes")
def skirt() -> list[str]:
    return [
        shadow(50, 84, 30),
        path("M34 22 H 66 L 82 80 Q 50 88 18 80 Z", PURPLE),
        *[path(f"M{44 + i * 6} 26 L {36 + i * 9} 82", "none", stroke="#6E50A3", sw=1.4) for i in range(3)],
        rect(32, 18, 36, 7, "#6E50A3", r=2),
    ]


@art("clothes_dress", "clothes")
def dress() -> list[str]:
    dots = [circle(x, y, 1.8, WHITE, op=0.8) for x, y in ((40, 56), (52, 64), (60, 54), (34, 72), (46, 76), (64, 74),
                                                               (56, 46), (44, 44))]
    return [
        *hanger(),
        path("M38 24 L 32 30 L 36 40 L 40 38 L 38 50 L 22 86 H 78 L 62 50 L 60 38 L 64 40 L 68 30 L 62 24 "
             "Q 50 32 38 24 Z", PINK),
        rect(37, 48, 26, 4, RED, r=1.5), *dots,
    ]


@art("clothes_shoe", "clothes")
def shoe() -> list[str]:
    return [
        shadow(50, 78, 36),
        path("M14 72 V 50 Q 14 40 24 40 H 40 Q 48 54 66 56 Q 86 58 88 68 V 72 Z", BROWN_DARK),
        path("M14 72 H 88 V 76 Q 88 78 86 78 H 16 Q 14 78 14 76 Z", BLACK),
        path("M24 40 H 40 Q 46 50 54 54 L 30 54 Q 24 48 24 40 Z", BROWN),
        stroke("M38 46 l 6 -2 M42 51 l 6 -2", OFF_WHITE, 1.6),
    ]


@art("object_gift", "object")
def gift() -> list[str]:
    return [
        shadow(50, 86, 30),
        rect(22, 46, 56, 40, RED, r=3), rect(18, 36, 64, 12, RED_DARK, r=3),
        rect(46, 36, 8, 50, YELLOW), path("M50 36 Q 34 20 30 30 Q 28 38 50 36 Z", YELLOW),
        path("M50 36 Q 66 20 70 30 Q 72 38 50 36 Z", YELLOW_DARK),
    ]


@art("object_birthday_cake", "object")
def birthday_cake() -> list[str]:
    return [
        shadow(50, 86, 34),
        rect(20, 52, 60, 32, PINK, r=4), path("M20 60 Q 30 66 40 60 T 60 60 T 80 60 V 56 Q 80 52 76 52 H 24 Q 20 52 20 56 Z", WHITE),
        rect(14, 82, 72, 5, GREY, r=2),
        *[rect(x, 36, 4, 16, c, r=1) for x, c in ((32, TEAL), (48, YELLOW), (64, PURPLE))],
        *[path(f"M{x + 2} 26 Q {x - 1} 31 {x + 2} 34 Q {x + 5} 31 {x + 2} 26 Z", "#FFA93A") for x in (32, 48, 64)],
    ]


COLORS = {
    "red": (RED, RED_DARK), "white": (WHITE, "#D9DDE2"), "black": (BLACK, "#111114"), "blue": (BLUE, BLUE_DARK),
    "green": (GREEN, GREEN_DARK), "yellow": (YELLOW, YELLOW_DARK), "grey": (GREY, GREY_DARK),
    "brown": (BROWN, BROWN_DARK),
}


def paint(fill: str, shade: str) -> list[str]:
    """A splash of paint: color pictures show nothing but the color itself."""
    return [
        shadow(50, 86, 30),
        path("M50 16 Q 66 14 74 26 Q 90 30 86 48 Q 92 64 78 74 Q 72 88 54 84 Q 36 92 26 78 Q 10 70 16 52 "
             "Q 8 34 24 28 Q 32 14 50 16 Z", fill, stroke=shade, sw=1.2),
        path("M78 74 Q 72 88 54 84 Q 36 92 26 78 Q 46 82 62 76 Q 78 70 86 56 Q 88 66 78 74 Z", shade),
        circle(84, 18, 5, fill), circle(14, 86, 4, fill), circle(90, 84, 3, fill),
        path("M32 36 Q 38 28 48 28", "none", stroke=WHITE, sw=4, op=0.35),
    ]


for _name, (_fill, _shade) in COLORS.items():
    art(f"color_{_name}", "color")(lambda fill=_fill, shade=_shade: paint(fill, shade))

