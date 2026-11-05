"""Weather, nature and recycling (units 2.2, 9.1)."""
from __future__ import annotations

from .kit import (
    BLUE, BLUE_DARK, BROWN, BROWN_DARK, GREEN, GREEN_DARK, GREEN_LIGHT, GREY, GREY_DARK, GREY_LIGHT, NAVY,
    OFF_WHITE, ORANGE, SKY, WHITE, YELLOW, YELLOW_DARK, art, circle, ground, line, path, rect, shadow, stroke,
)


def cloud(cx: float, cy: float, s: float = 1.0, color: str = WHITE) -> list[str]:
    return [circle(cx - 12 * s, cy + 2 * s, 9 * s, color), circle(cx, cy - 4 * s, 12 * s, color),
            circle(cx + 12 * s, cy + 2 * s, 9 * s, color), rect(cx - 20 * s, cy + 2 * s, 40 * s, 9 * s, color, r=4 * s)]


@art("weather_sun", "weather")
def sun() -> list[str]:
    rays = [line(50 + 26 * dx, 46 + 26 * dy, 50 + 36 * dx, 46 + 36 * dy, YELLOW_DARK, 4)
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1), (0.7, 0.7), (-0.7, 0.7), (0.7, -0.7), (-0.7, -0.7))]
    return [*rays, circle(50, 46, 20, YELLOW), circle(44, 40, 6, "#FFE58A", op=0.6), ground(GREEN_LIGHT, 92)]


@art("weather_rain", "weather")
def rain() -> list[str]:
    drops = [path(f"M{x} {y} q -3 6 0 8 q 3 -2 0 -8 z", BLUE) for x, y in
             ((32, 56), (46, 62), (60, 56), (74, 64), (38, 74), (54, 78), (68, 80), (26, 70))]
    return [rect(0, 0, 100, 100, "#C9D6E2"), *cloud(50, 36, 1.4, GREY), *cloud(54, 34, 1.15, GREY_LIGHT), *drops]


@art("weather_snow", "weather")
def snow() -> list[str]:
    flakes = []
    for x, y, s in ((26, 22, 5), (70, 18, 6), (48, 34, 4), (84, 44, 4), (16, 48, 4), (60, 52, 5)):
        flakes += [line(x - s, y, x + s, y, WHITE, 1.6), line(x - s * 0.5, y - s * 0.86, x + s * 0.5, y + s * 0.86, WHITE, 1.6),
                   line(x - s * 0.5, y + s * 0.86, x + s * 0.5, y - s * 0.86, WHITE, 1.6)]
    snowman = [circle(50, 76, 13, WHITE), circle(50, 56, 9, WHITE), circle(47, 54, 1.2, NAVY), circle(53, 54, 1.2, NAVY),
               path("M50 57 l 6 1.5 l -6 1.5 z", ORANGE), rect(41, 63, 18, 3, "#C8102E", r=1.5)]
    return [rect(0, 0, 100, 100, "#BFD7EA"), *flakes, path("M0 80 Q 50 70 100 80 V 100 H 0 Z", WHITE), *snowman]


@art("weather_wind", "weather")
def wind() -> list[str]:
    return [
        stroke("M10 34 H 58 Q 70 34 70 24 Q 70 16 62 16 Q 56 16 56 22", "#7FA9C9", 4),
        stroke("M10 50 H 76 Q 88 50 88 60 Q 88 70 78 70 Q 72 70 72 64", "#7FA9C9", 4),
        stroke("M18 66 H 52", "#7FA9C9", 4),
        path("M30 80 Q 36 72 44 78 Q 38 86 30 80 Z", GREEN), path("M60 86 Q 64 78 72 82 Q 66 90 60 86 Z", ORANGE),
    ]


@art("weather_cold", "weather")
def cold() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#CFE3F2"),
        rect(42, 12, 16, 60, WHITE, r=8), circle(50, 74, 13, WHITE),
        rect(47, 50, 6, 26, BLUE, r=3), circle(50, 74, 9, BLUE),
        *[line(58, 18 + i * 8, 62, 18 + i * 8, GREY, 1.4) for i in range(6)],
        *[line(x - 5, y, x + 5, y, WHITE, 1.6) for x, y in ((24, 30), (78, 40), (22, 66))],
        *[line(x, y - 5, x, y + 5, WHITE, 1.6) for x, y in ((24, 30), (78, 40), (22, 66))],
    ]


@art("weather_hot", "weather")
def hot() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#FDE3C4"), circle(80, 18, 10, YELLOW),
        rect(42, 12, 16, 60, WHITE, r=8), circle(50, 74, 13, WHITE),
        rect(47, 22, 6, 54, "#C8102E", r=3), circle(50, 74, 9, "#C8102E"),
        *[line(58, 18 + i * 8, 62, 18 + i * 8, GREY, 1.4) for i in range(6)],
        stroke("M20 40 q 4 -6 0 -12 q -4 -6 0 -12 M30 56 q 4 -6 0 -12 q -4 -6 0 -12", ORANGE, 2.4),
    ]


@art("nature_tree", "nature")
def tree() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKY), ground(GREEN_LIGHT, 86),
        path("M44 88 L 46 54 H 54 L 56 88 Z", BROWN_DARK),
        circle(50, 40, 24, GREEN), circle(34, 50, 14, GREEN), circle(66, 50, 14, GREEN), circle(58, 30, 12, "#73B97F"),
        circle(40, 36, 3, "#C8102E"), circle(62, 48, 3, "#C8102E"), circle(48, 54, 3, "#C8102E"),
    ]


@art("nature_forest", "nature")
def forest() -> list[str]:
    def pine(x: float, base: float, h: float, color: str) -> list[str]:
        return [rect(x - 2, base - 4, 4, 8, BROWN_DARK),
                path(f"M{x} {base - h} L {x + h * 0.32} {base - 2} H {x - h * 0.32} Z", color)]
    return [rect(0, 0, 100, 100, SKY), ground(GREEN, 70), rect(0, 74, 100, 26, GREEN),
            *pine(18, 70, 40, GREEN_DARK), *pine(40, 66, 48, "#356B42"), *pine(64, 70, 42, GREEN_DARK),
            *pine(86, 68, 36, "#356B42"), *pine(30, 92, 34, "#2F5E3A"), *pine(56, 94, 38, "#2F5E3A"),
            *pine(80, 94, 30, "#2F5E3A")]


@art("nature_sea", "nature")
def sea() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKY), circle(24, 22, 8, YELLOW),
        rect(0, 44, 100, 56, "#3F93C8"),
        *[path(f"M0 {y} Q 12 {y - 5} 25 {y} T 50 {y} T 75 {y} T 100 {y} V 100 H 0 Z", c)
          for y, c in ((56, "#4BA3D1"), (72, "#2F7FB4"), (88, "#246A9A"))],
        path("M62 44 L 70 22 L 70 44 Z", WHITE), path("M70 26 L 80 42 H 70 Z", OFF_WHITE),
        path("M58 44 H 84 L 80 50 H 62 Z", "#C8102E"),
    ]


@art("nature_river", "nature")
def river() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKY), path("M0 40 Q 30 24 60 36 T 100 30 V 100 H 0 Z", GREEN),
        path("M54 36 Q 46 46 54 56 Q 66 70 40 82 Q 26 90 22 100 H 62 Q 64 90 76 80 Q 92 66 74 52 Q 64 44 64 36 Z", "#4BA3D1"),
        stroke("M58 50 q 4 4 0 8 M50 80 q 6 -2 10 -6", WHITE, 1.6, op=0.7),
        circle(18, 60, 8, GREEN_DARK), rect(16, 66, 4, 8, BROWN_DARK), circle(86, 74, 7, GREEN_DARK), rect(84, 79, 4, 7, BROWN_DARK),
    ]


@art("object_bottle", "object")
def bottle() -> list[str]:
    return [
        shadow(50, 90, 16),
        rect(44, 10, 12, 8, "#2E7D4F", r=1.5),
        path("M44 18 H 56 V 30 Q 64 36 64 46 V 86 Q 64 90 60 90 H 40 Q 36 90 36 86 V 46 Q 36 36 44 30 Z", "#5FAE7F", op=0.9),
        rect(40, 40, 3, 42, WHITE, r=1.5, op=0.5), rect(36, 54, 28, 16, OFF_WHITE),
    ]


@art("object_bag", "object")
def bag() -> list[str]:
    return [
        shadow(50, 90, 26),
        path("M38 36 Q 38 18 50 18 Q 62 18 62 36", "none", stroke=BROWN_DARK, sw=3.4),
        path("M24 34 H 76 L 80 88 H 20 Z", "#D9B88A"), path("M66 34 H 76 L 80 88 H 70 Z", "#C2A274"),
        path("M36 60 Q 44 50 50 60 Q 56 50 64 60 Q 50 76 36 60 Z", GREEN, op=0.8),
    ]


@art("object_paper", "object")
def paper() -> list[str]:
    return [
        rect(26, 18, 50, 66, WHITE, r=2, transform="rotate(-6 51 51)"),
        rect(22, 14, 50, 66, OFF_WHITE, r=2, transform="rotate(4 47 47)"),
        path("M30 14 H 64 L 74 24 V 82 H 30 Z", WHITE), path("M64 14 V 24 H 74 Z", GREY_LIGHT),
        *[rect(36, 32 + i * 9, 30 - (i % 3) * 6, 2.4, GREY_LIGHT, r=1.2) for i in range(5)],
    ]


@art("object_bin", "object")
def bin_() -> list[str]:
    return [
        shadow(50, 90, 24),
        rect(26, 20, 48, 8, GREY_DARK, r=3), rect(42, 14, 16, 6, GREY_DARK, r=2),
        path("M30 30 H 70 L 66 88 H 34 Z", GREY), *[rect(40 + i * 8, 38, 3, 42, GREY_DARK, r=1.5, op=0.5) for i in range(3)],
    ]


@art("object_container", "object")
def container() -> list[str]:
    def bin_box(x: float, color: str, dark: str) -> list[str]:
        return [rect(x, 34, 26, 50, color, r=3), rect(x - 2, 28, 30, 8, dark, r=2), circle(x + 5, 88, 3, "#2A2A2E"),
                circle(x + 21, 88, 3, "#2A2A2E")]
    arrows = stroke("M45 60 a 6 6 0 1 1 6 6 M51 66 l -3 -3 M51 66 l -3 3", WHITE, 1.8)
    return [ground(GREY_LIGHT, 92), *bin_box(8, YELLOW, YELLOW_DARK), *bin_box(37, BLUE, BLUE_DARK),
            *bin_box(66, GREEN, GREEN_DARK), arrows]


@art("nature_wood", "nature")
def wood() -> list[str]:
    def log(cx: float, cy: float) -> list[str]:
        return [rect(cx - 32, cy - 9, 56, 18, BROWN, r=3), circle(cx + 24, cy, 9, "#E0B98A"),
                circle(cx + 24, cy, 5.5, "none", stroke=BROWN_LIGHT_RING, sw=1.2), circle(cx + 24, cy, 2, BROWN_LIGHT_RING)]
    return [shadow(50, 86, 38), *log(46, 76), *log(56, 76), *log(51, 58)]


BROWN_LIGHT_RING = "#B98A5A"
