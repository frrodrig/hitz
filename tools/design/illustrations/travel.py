"""Transport, leisure and everyday objects (units 1.2, 6.1, 7.1, 10.1, 10.2, 11.2, 12.1, 12.2)."""
from __future__ import annotations

from .kit import (
    BLACK, BLUE, BLUE_DARK, BROWN, BROWN_DARK, GREEN, GREEN_DARK, GREEN_LIGHT, GREY, GREY_DARK, GREY_LIGHT,
    NAVY, NAVY_LIGHT, OFF_WHITE, ORANGE, ORANGE_DARK, PINK, PURPLE, RED, RED_DARK, SKY, TEAL, WHITE,
    YELLOW, YELLOW_DARK, art, circle, ellipse, figure, ground, line, path, poly, rect, shadow, stroke,
)


def wheel(cx: float, cy: float, r: float) -> list[str]:
    return [circle(cx, cy, r, BLACK), circle(cx, cy, r * 0.45, GREY_LIGHT)]


@art("transport_bus", "transport")
def bus() -> list[str]:
    return [
        rect(0, 82, 100, 18, GREY_DARK), *[rect(6 + i * 22, 90, 10, 2, WHITE) for i in range(5)],
        rect(8, 26, 84, 50, YELLOW, r=7), rect(8, 64, 84, 8, YELLOW_DARK),
        *[rect(14 + i * 17, 34, 13, 14, SKY, r=2) for i in range(4)], rect(82, 34, 8, 24, SKY, r=2),
        rect(16, 54, 68, 3, ORANGE_DARK, r=1.5), circle(88, 66, 2.4, WHITE),
        *wheel(26, 78, 7), *wheel(74, 78, 7),
    ]


@art("transport_train", "transport")
def train() -> list[str]:
    return [
        rect(0, 84, 100, 3, GREY_DARK), *[rect(4 + i * 12, 87, 6, 4, BROWN) for i in range(8)],
        path("M10 30 H 72 Q 92 30 94 60 V 76 H 10 Z", RED), path("M10 66 H 94 V 76 H 10 Z", RED_DARK),
        *[rect(16 + i * 16, 38, 11, 14, SKY, r=2) for i in range(3)], path("M68 38 H 76 Q 86 40 88 54 H 68 Z", SKY),
        rect(10, 58, 84, 3, WHITE), *wheel(24, 78, 6), *wheel(46, 78, 6), *wheel(76, 78, 6),
    ]


@art("transport_metro", "transport")
def metro() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#3A4256"), path("M0 26 Q 50 -10 100 26 V 100 H 0 Z", "#4C566D"),
        rect(0, 86, 100, 14, GREY_DARK),
        rect(16, 34, 68, 50, OFF_WHITE, r=8), rect(16, 72, 68, 12, GREY_LIGHT, r=4),
        rect(24, 42, 52, 18, "#2E3E5C", r=3), rect(30, 22, 40, 10, BLUE, r=2), rect(46.5, 24.5, 7, 5, WHITE, r=1),
        circle(28, 72, 4, YELLOW), circle(72, 72, 4, YELLOW), rect(40, 66, 20, 3, BLUE),
    ]


@art("transport_car", "transport")
def car() -> list[str]:
    return [
        rect(0, 82, 100, 18, GREY_DARK),
        path("M8 72 V 60 Q 8 52 18 50 L 28 34 Q 32 28 40 28 H 62 Q 70 28 74 34 L 84 50 Q 94 52 94 62 V 72 Z", BLUE),
        path("M32 48 L 38 36 Q 40 33 44 33 H 50 V 48 Z", SKY), path("M54 33 H 60 Q 64 33 66 36 L 72 48 H 54 Z", SKY),
        rect(8, 62, 86, 6, BLUE_DARK), circle(90, 58, 2.6, YELLOW), rect(8, 56, 5, 4, RED, r=1),
        *wheel(28, 74, 9), *wheel(74, 74, 9),
    ]


@art("transport_bicycle", "transport")
def bicycle() -> list[str]:
    return [
        ground(GREY_LIGHT, 92),
        circle(26, 66, 17, "none", stroke=BLACK, sw=3.4), circle(76, 66, 17, "none", stroke=BLACK, sw=3.4),
        stroke("M26 66 L 42 40 H 68 L 76 66 M42 40 L 50 66 L 68 40 M50 66 H 26", RED, 3.4),
        stroke("M42 40 L 38 30 M32 30 H 44", BLACK, 3), stroke("M68 40 L 66 30 Q 66 26 72 26", GREY_DARK, 3),
        circle(50, 66, 3.4, GREY_DARK),
    ]


@art("transport_plane", "transport")
def plane() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKY), circle(22, 72, 8, WHITE, op=0.9), circle(32, 70, 10, WHITE, op=0.9), circle(80, 24, 7, WHITE, op=0.9),
        path("M12 52 Q 12 44 22 44 H 80 Q 94 46 94 52 Q 94 58 80 60 H 22 Q 12 60 12 52 Z", WHITE),
        path("M44 46 L 58 20 H 66 L 60 46 Z", GREY_LIGHT), path("M44 58 L 58 84 H 66 L 60 58 Z", GREY_LIGHT),
        path("M12 52 L 8 34 H 16 L 26 46 Z", RED),
        *[circle(34 + i * 8, 50, 2, BLUE) for i in range(6)], path("M84 48 H 90 Q 93 50 92 52 H 84 Z", NAVY),
    ]


@art("leisure_football", "leisure")
def football() -> list[str]:
    patches = [poly("50,40 58,46 55,56 45,56 42,46", BLACK)] + [
        poly(f"{50 + 22 * dx},{50 + 22 * dy} {50 + 28 * dx - 4 * dy},{50 + 28 * dy + 4 * dx} "
             f"{50 + 28 * dx + 4 * dy},{50 + 28 * dy - 4 * dx}", BLACK)
        for dx, dy in ((0, -1), (0.95, -0.31), (0.59, 0.81), (-0.59, 0.81), (-0.95, -0.31))]
    return [rect(0, 70, 100, 30, GREEN), stroke("M0 86 H 100", WHITE, 1.4, op=0.7), shadow(50, 80, 26),
            circle(50, 50, 28, WHITE), *patches, circle(50, 50, 28, "none", stroke=GREY, sw=1)]


@art("leisure_basketball", "leisure")
def basketball() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#F7D9B4"),
        rect(56, 8, 34, 24, WHITE, r=2), rect(66, 16, 14, 10, "none", stroke="#C8102E", sw=1.6),
        ellipse(73, 34, 10, 3, "none", stroke="#C8102E", sw=2.4),
        stroke("M64 34 L 68 46 L 73 36 L 78 46 L 82 34", WHITE, 1, op=0.9),
        circle(36, 62, 24, ORANGE),
        stroke("M12 62 H 60 M36 38 V 86 M19 45 Q 32 62 19 79 M53 45 Q 40 62 53 79", BROWN_DARK, 1.8),
    ]


@art("leisure_swimming", "leisure")
def swimming() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#4BB3D6"),
        *[path(f"M0 {y} q 12 -5 25 0 t 25 0 t 25 0 t 25 0", "none", stroke=WHITE, sw=1.8, op=0.5) for y in (24, 80)],
        path("M14 56 Q 30 50 50 54 Q 66 56 80 50", "none", stroke="#F2C49B", sw=6),
        circle(56, 46, 9, "#F2C49B"), path("M47 44 Q 48 35 56 35 Q 64 35 65 44 Z", "#C8102E"),
        rect(52, 44, 10, 3.5, NAVY, r=1.5), stroke("M66 46 Q 80 34 88 42", "#F2C49B", 5),
        path("M8 62 Q 30 56 52 62 T 96 62", "none", stroke=WHITE, sw=2.4, op=0.8),
    ]


@art("leisure_cycling", "leisure")
def cycling() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKY), path("M0 70 Q 50 50 100 66 V 100 H 0 Z", GREEN_LIGHT),
        path("M0 84 Q 50 72 100 82 V 100 H 0 Z", GREY_DARK),
        circle(28, 74, 12, "none", stroke=BLACK, sw=2.6), circle(70, 72, 12, "none", stroke=BLACK, sw=2.6),
        stroke("M28 74 L 40 54 H 60 L 70 72 M40 54 L 48 74 L 60 54", YELLOW, 2.8),
        stroke("M44 50 L 56 36 L 64 48 M44 50 L 50 64 M56 36 L 48 58", "#F2C49B", 4),
        path("M50 28 Q 50 20 58 20 Q 66 20 66 28 Z", "#C8102E"), circle(58, 30, 5, "#F2C49B"),
        path("M42 48 L 58 34 L 64 40 L 48 54 Z", "#C8102E"),
    ]


@art("leisure_dance", "leisure")
def dance() -> list[str]:
    notes = [path("M18 20 v 14 a 4 3 0 1 1 -2 -2.6 V 18 l 10 -3 v 4 z", PURPLE),
             path("M80 30 v 12 a 4 3 0 1 1 -2 -2.6 V 30 z", PURPLE)]
    return [*notes, *figure(38, 90, 66, hair_style="long", hair_color="#2B2522", shirt="#C8102E", skirt=True),
            *figure(64, 90, 70, hair_color="#5B3A29", shirt=NAVY_LIGHT, legs=NAVY),
            stroke("M46 50 Q 52 40 56 48", "#F2C49B", 3)]


@art("leisure_music", "leisure")
def music() -> list[str]:
    return [
        path("M36 26 L 76 16 V 64", "none", stroke=NAVY, sw=4, join="round"), line(36, 26, 36, 74, NAVY, 4),
        ellipse(28, 74, 10, 7.5, NAVY, transform="rotate(-20 28 74)"), ellipse(68, 64, 10, 7.5, NAVY, transform="rotate(-20 68 64)"),
        path("M36 26 L 76 16 V 26 L 36 36 Z", NAVY),
        circle(18, 30, 3, YELLOW), circle(86, 82, 3.5, PINK), circle(84, 40, 2.4, TEAL),
    ]


@art("leisure_concert", "leisure")
def concert() -> list[str]:
    crowd = [circle(8 + i * 12, 90 - (i % 2) * 3, 7, "#4A3F63") for i in range(9)]
    hands = [stroke(f"M{14 + i * 24} 84 L {10 + i * 24} 72", "#4A3F63", 3) for i in range(4)]
    return [
        rect(0, 0, 100, 100, "#2A1F3D"),
        path("M20 0 L 34 60 H 6 Z", YELLOW, op=0.25), path("M80 0 L 94 60 H 66 Z", PINK, op=0.25),
        rect(0, 60, 100, 8, BROWN_DARK),
        *figure(46, 60, 48, hair_style="long", hair_color="#E0B45A", shirt=RED),
        rect(58, 30, 2, 30, GREY), circle(59, 29, 3, GREY_DARK),
        *crowd, *hands,
    ]


@art("leisure_film", "leisure")
def film() -> list[str]:
    holes = [rect(x, y, 5, 5, "#1E2433", r=1) for x in (14, 81) for y in range(18, 84, 10)]
    return [
        rect(10, 12, 80, 76, BLACK, r=4), *holes,
        rect(22, 16, 56, 32, SKY), poly("26,46 40,28 50,40 56,34 74,46", GREEN), circle(64, 24, 4, YELLOW),
        rect(22, 52, 56, 32, "#FCE3DF"), circle(50, 64, 7, "#F2C49B"), rect(42, 72, 16, 12, RED, r=3),
    ]


@art("object_phone", "object")
def phone() -> list[str]:
    return [
        shadow(50, 90, 20),
        rect(30, 10, 40, 78, NAVY, r=7), rect(33.5, 17, 33, 60, "#CFE8F5", r=2),
        rect(44, 12.5, 12, 2, GREY, r=1), circle(50, 82.5, 3, GREY),
        *[rect(37 + c * 10, 22 + r * 11, 7, 7, col, r=2) for r, row in enumerate(((RED, TEAL, YELLOW), (GREEN, PURPLE, ORANGE)))
          for c, col in enumerate(row)],
        path("M42 60 q 0 -4 4 -4 h 8 q 4 0 4 4 v 6 q 0 4 -4 4 h -6 l -4 3 v -3 q -2 -1 -2 -4 z", GREEN),
    ]


@art("object_book", "object")
def book() -> list[str]:
    return [
        shadow(50, 86, 34),
        path("M14 26 Q 32 20 50 28 V 82 Q 32 74 14 80 Z", WHITE), path("M86 26 Q 68 20 50 28 V 82 Q 68 74 86 80 Z", OFF_WHITE),
        path("M14 80 Q 32 74 50 82 Q 68 74 86 80 V 84 Q 68 78 50 86 Q 32 78 14 84 Z", RED),
        *[stroke(f"M20 {36 + i * 8} Q 32 {32 + i * 8} 44 {37 + i * 8}", GREY_LIGHT, 1.8) for i in range(5)],
        *[stroke(f"M56 {37 + i * 8} Q 68 {32 + i * 8} 80 {36 + i * 8}", GREY_LIGHT, 1.8) for i in range(5)],
    ]


@art("object_computer", "object")
def computer() -> list[str]:
    return [
        shadow(50, 86, 38),
        rect(18, 18, 64, 44, GREY_DARK, r=4), rect(22, 22, 56, 36, "#6FA6D9", r=1),
        *[rect(28, 28 + i * 7, 26 - i * 4, 3, WHITE, r=1.5, op=0.85) for i in range(3)], rect(58, 28, 14, 14, YELLOW, r=1.5),
        path("M12 66 H 88 L 94 80 H 6 Z", GREY), rect(6, 80, 88, 4, GREY_DARK, r=2), rect(40, 72, 20, 4, GREY_DARK, r=1),
    ]


@art("object_card", "object")
def card() -> list[str]:
    return [
        shadow(50, 80, 36),
        rect(12, 26, 76, 48, BLUE, r=6, transform="rotate(-6 50 50)"),
        rect(12, 36, 76, 8, NAVY, transform="rotate(-6 50 50)"),
        rect(20, 50, 12, 9, YELLOW, r=1.5, transform="rotate(-6 50 50)"),
        *[rect(22 + i * 14, 64, 10, 3, WHITE, r=1.5, op=0.8, transform="rotate(-6 50 50)") for i in range(4)],
        stroke("M70 50 q 3 4 0 8 M74 47 q 5 7 0 14 M78 44 q 7 10 0 20", WHITE, 1.8, transform="rotate(-6 50 50)"),
    ]


@art("object_money", "object")
def money() -> list[str]:
    note = lambda y, rot: [rect(14, y, 64, 32, "#7FBF8E", r=3, transform=f"rotate({rot} 46 {y + 16})"),  # noqa: E731
                           rect(18, y + 4, 56, 24, "#A8D5B1", r=2, transform=f"rotate({rot} 46 {y + 16})"),
                           circle(46, y + 16, 7, "#7FBF8E", transform=f"rotate({rot} 46 {y + 16})")]
    coins = [circle(76, 74, 11, YELLOW_DARK), circle(76, 72, 11, YELLOW), circle(76, 72, 7, YELLOW_DARK, op=0.5),
             circle(62, 82, 8, GREY), circle(62, 80.5, 8, GREY_LIGHT)]
    return [shadow(50, 88, 36), *note(30, -8), *note(44, 4), *coins]


@art("object_suitcase", "object")
def suitcase() -> list[str]:
    return [
        shadow(50, 90, 26),
        path("M40 26 V 18 Q 40 14 44 14 H 56 Q 60 14 60 18 V 26", "none", stroke=GREY_DARK, sw=4),
        rect(22, 26, 56, 58, ORANGE, r=6), *[rect(34 + i * 14, 26, 4, 58, ORANGE_DARK) for i in range(3)],
        circle(30, 88, 3.4, BLACK), circle(70, 88, 3.4, BLACK),
        rect(28, 40, 12, 8, WHITE, r=1.5, transform="rotate(-10 34 44)"),
    ]


@art("object_tent", "nature")
def tent() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#1E2A44"), *[circle(x, y, 1, WHITE) for x, y in ((14, 14), (40, 10), (70, 18), (86, 8), (24, 30))],
        circle(78, 22, 6, "#FFF2C4"), ground(GREEN_DARK, 82),
        poly("10,84 46,26 82,84", ORANGE), poly("46,26 82,84 64,84", ORANGE_DARK), poly("38,84 46,52 54,84", "#5A3A1A"),
        line(46, 26, 46, 18, GREY_LIGHT, 1.6),
        path("M84 86 L 88 76 L 92 86 Z", YELLOW), path("M86 86 L 88 80 L 90 86 Z", ORANGE),
    ]


@art("object_backpack", "nature")
def backpack() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKY), path("M0 66 L 26 34 L 50 62 L 70 40 L 100 72 V 100 H 0 Z", GREEN),
        path("M38 30 Q 38 18 50 18 Q 62 18 62 30", "none", stroke=RED_DARK, sw=4),
        rect(30, 28, 40, 58, RED, r=10), rect(36, 56, 28, 22, RED_DARK, r=4), rect(36, 40, 28, 6, RED_DARK, r=3),
        rect(46, 60, 8, 3, YELLOW, r=1.5),
    ]


@art("object_clock", "object")
def clock() -> list[str]:
    ticks = [line(50 + 26 * dx, 52 + 26 * dy, 50 + 30 * dx, 52 + 30 * dy, NAVY, 2.4)
             for dx, dy in ((0, -1), (1, 0), (0, 1), (-1, 0))]
    return [shadow(50, 90, 26), circle(50, 52, 36, RED), circle(50, 52, 31, WHITE), *ticks,
            line(50, 52, 50, 32, NAVY, 3.4), line(50, 52, 64, 58, NAVY, 3.4), circle(50, 52, 3, RED)]


@art("object_photo", "object")
def photo() -> list[str]:
    return [
        shadow(50, 86, 32),
        rect(20, 18, 56, 62, WHITE, r=2, transform="rotate(-8 48 49)"),
        rect(25, 23, 46, 42, SKY, transform="rotate(-8 48 49)"),
        poly("27,62 42,42 52,54 58,48 70,60", GREEN, transform="rotate(-8 48 49)"),
        circle(60, 34, 5, YELLOW, transform="rotate(-8 48 49)"),
    ]
