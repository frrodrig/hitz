"""The house, its rooms and furniture (units 4.1, 4.2)."""
from __future__ import annotations

from .kit import (
    BLACK, BLUE, BROWN, BROWN_DARK, BROWN_LIGHT, GREEN, GREEN_DARK, GREEN_LIGHT, GREY, GREY_DARK, GREY_LIGHT,
    NAVY, OFF_WHITE, ORANGE, RED, RED_DARK, SKY, TEAL, TEAL_DARK, TEAL_LIGHT, WHITE, YELLOW, art, circle,
    door, ellipse, ground, line, path, poly, rect, shadow, stroke, window,
)

FLOOR = "#D8C3A5"


@art("home_house", "home")
def house() -> list[str]:
    return [
        ground(GREEN_LIGHT, 84),
        rect(24, 44, 52, 42, OFF_WHITE),
        poly("16,46 50,18 84,46", RED), poly("50,18 84,46 76,46 50,25", RED_DARK),
        rect(64, 22, 8, 14, BROWN_DARK),
        *window(30, 52, 12, 12), *window(58, 52, 12, 12),
        *door(44, 64, 12, 22, BROWN),
        # Green wooden shutters, as on many Basque farmhouses.
        rect(27, 52, 3, 12, GREEN_DARK), rect(42, 52, 3, 12, GREEN_DARK),
        rect(55, 52, 3, 12, GREEN_DARK), rect(70, 52, 3, 12, GREEN_DARK),
    ]


@art("home_window", "home")
def window_art() -> list[str]:
    return [
        rect(18, 16, 64, 68, WHITE, r=4), rect(23, 21, 54, 58, SKY, r=2),
        path("M23 64 Q 40 54 56 62 T 77 58 V 79 H 23 Z", GREEN_LIGHT),
        circle(64, 34, 6, YELLOW),
        rect(48, 21, 4, 58, WHITE), rect(23, 48, 54, 4, WHITE),
        rect(14, 84, 72, 6, GREY_LIGHT, r=2),
        path("M23 21 H 38 L 23 40 Z", WHITE, op=0.35),
    ]


@art("home_door", "home")
def door_art() -> list[str]:
    return [
        rect(0, 86, 100, 14, FLOOR),
        rect(26, 10, 48, 78, BROWN_DARK, r=2), rect(30, 14, 40, 74, BROWN),
        rect(35, 20, 30, 24, BROWN_LIGHT, r=2, op=0.6), rect(35, 52, 30, 28, BROWN_LIGHT, r=2, op=0.6),
        circle(63, 50, 3, YELLOW), rect(40, 30, 20, 4, YELLOW, r=1),
        rect(22, 86, 56, 4, GREY, r=1),
    ]


@art("home_stairs", "home")
def stairs() -> list[str]:
    steps = []
    for i in range(6):
        x, y = 14 + i * 12, 84 - i * 12
        steps += [rect(x, y, 86 - x, 12, BROWN_LIGHT), rect(x, y, 86 - x, 3, BROWN)]
    return [*steps, stroke("M20 70 L 80 10", BROWN_DARK, 2.6), *[line(20 + i * 12, 70 - i * 12, 20 + i * 12, 82 - i * 12, BROWN_DARK, 1.6) for i in range(6)]]


@art("home_elevator", "home")
def elevator() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#E7DCCB"),
        rect(22, 18, 48, 72, GREY_DARK, r=2), rect(25, 21, 20.5, 69, GREY), rect(46.5, 21, 20.5, 69, GREY),
        rect(45.5, 21, 1, 69, GREY_DARK),
        rect(36, 8, 20, 7, NAVY, r=1.5), poly("41,13 44,10 47,13", "#6FE07A"), poly("49,10 52,13 55,10", "#6FE07A", op=0.35),
        rect(76, 44, 10, 20, GREY_LIGHT, r=2), circle(81, 50, 2.4, NAVY), circle(81, 58, 2.4, NAVY),
        poly("79.5,51 81,48.8 82.5,51", WHITE), poly("79.5,57 81,59.2 82.5,57", WHITE),
    ]


@art("home_table", "home")
def table() -> list[str]:
    return [
        shadow(50, 88, 38),
        rect(10, 40, 80, 8, BROWN, r=2), rect(10, 46, 80, 4, BROWN_DARK),
        rect(16, 50, 6, 36, BROWN_DARK), rect(78, 50, 6, 36, BROWN_DARK),
        path("M38 40 Q 38 28 46 26 H 54 Q 62 28 62 40 Z", TEAL), path("M44 26 Q 50 16 56 26", "none", stroke=GREEN, sw=3),
        circle(46, 20, 4, RED), circle(54, 18, 4, YELLOW), circle(50, 14, 4, ORANGE),
    ]


@art("home_bed", "home")
def bed() -> list[str]:
    return [
        rect(0, 86, 100, 14, FLOOR),
        rect(10, 34, 8, 52, BROWN_DARK, r=2), rect(82, 50, 8, 36, BROWN_DARK, r=2),
        rect(14, 56, 72, 16, WHITE), rect(14, 70, 72, 6, BROWN),
        path("M38 50 H 84 Q 88 50 88 56 V 70 H 38 Z", BLUE), path("M38 50 H 84 Q 88 50 88 56 V 58 H 38 Z", "#5E96E3"),
        rect(18, 46, 20, 12, OFF_WHITE, r=5),
    ]


@art("home_chair", "home")
def chair() -> list[str]:
    return [
        shadow(50, 88, 24),
        rect(32, 14, 6, 50, BROWN_DARK, r=2), rect(62, 14, 6, 50, BROWN_DARK, r=2),
        rect(32, 18, 36, 6, BROWN, r=2), rect(32, 30, 36, 6, BROWN, r=2),
        rect(28, 54, 44, 8, BROWN, r=2), rect(28, 60, 44, 3, BROWN_DARK),
        rect(30, 62, 5, 26, BROWN_DARK), rect(65, 62, 5, 26, BROWN_DARK),
    ]


@art("home_sofa", "home")
def sofa() -> list[str]:
    return [
        shadow(50, 86, 40),
        rect(14, 30, 72, 32, TEAL_DARK, r=8),
        rect(8, 46, 16, 32, TEAL, r=6), rect(76, 46, 16, 32, TEAL, r=6),
        rect(22, 54, 28, 16, TEAL, r=4), rect(50, 54, 28, 16, TEAL, r=4),
        rect(14, 70, 72, 10, TEAL_DARK, r=3),
        rect(14, 80, 5, 6, BROWN_DARK), rect(81, 80, 5, 6, BROWN_DARK),
        rect(28, 36, 16, 14, YELLOW, r=3, transform="rotate(-8 36 43)"),
    ]


@art("home_wardrobe", "home")
def wardrobe() -> list[str]:
    return [
        rect(0, 88, 100, 12, FLOOR),
        rect(22, 10, 56, 76, BROWN, r=2), rect(22, 10, 56, 6, BROWN_DARK, r=2),
        rect(25, 19, 24, 64, BROWN_LIGHT, r=1), rect(51, 19, 24, 64, BROWN_LIGHT, r=1),
        rect(45, 44, 2, 10, BROWN_DARK, r=1), rect(53, 44, 2, 10, BROWN_DARK, r=1),
        rect(26, 86, 6, 4, BROWN_DARK), rect(68, 86, 6, 4, BROWN_DARK),
    ]


@art("home_fridge", "home")
def fridge() -> list[str]:
    return [
        rect(0, 90, 100, 10, FLOOR),
        rect(28, 8, 44, 82, "#EEF2F6", r=5), rect(56, 8, 16, 82, GREY_LIGHT, r=5),
        rect(28, 36, 44, 2, GREY),
        rect(32, 18, 3, 12, GREY_DARK, r=1.5), rect(32, 44, 3, 18, GREY_DARK, r=1.5),
        circle(46, 22, 3, RED), rect(50, 50, 10, 8, YELLOW, r=1, transform="rotate(8 55 54)"),
    ]


@art("home_washing_machine", "home")
def washing_machine() -> list[str]:
    return [
        rect(0, 88, 100, 12, FLOOR),
        rect(20, 14, 60, 74, "#F3F5F8", r=5), rect(20, 14, 60, 14, GREY_LIGHT, r=5),
        rect(26, 18, 18, 6, NAVY, r=1.5), circle(68, 21, 3.5, GREY_DARK),
        circle(50, 58, 22, GREY), circle(50, 58, 17, "#7DBAD8"),
        path("M33 62 Q 42 54 50 62 T 67 62 A 17 17 0 0 1 33 62 Z", "#4C97BF"),
        circle(43, 52, 3, WHITE, op=0.6),
    ]


@art("home_oven", "home")
def oven() -> list[str]:
    return [
        rect(0, 88, 100, 12, FLOOR),
        rect(18, 18, 64, 70, GREY_DARK, r=4), rect(18, 18, 64, 14, BLACK, r=4),
        *[circle(30 + i * 13, 25, 3.2, GREY) for i in range(4)],
        rect(26, 40, 48, 38, "#3B3F46", r=3), rect(30, 44, 40, 30, "#FFB04A", r=2, op=0.55),
        rect(30, 36, 40, 3, GREY_LIGHT, r=1.5),
        path("M38 66 Q 50 56 62 66 Z", BROWN_LIGHT),
    ]


@art("home_shower", "home")
def shower() -> list[str]:
    drops = [line(40 + (i % 4) * 7, 36 + (i // 4) * 12, 38 + (i % 4) * 7, 42 + (i // 4) * 12, "#5DB4E0", 2) for i in range(16)]
    return [
        *[rect(0, y, 100, 1, WHITE, op=0.6) for y in range(12, 100, 12)],
        rect(0, 86, 100, 14, TEAL_LIGHT),
        stroke("M20 90 V 14 Q 20 8 28 8 H 52 V 18", GREY_DARK, 3.4),
        path("M40 18 H 64 L 60 26 H 44 Z", GREY), *drops,
    ]


@art("room_kitchen", "home")
def kitchen() -> list[str]:
    return [
        rect(0, 0, 100, 56, "#F2E6D6"), *[rect(x, 30, 10, 10, WHITE, op=0.6) for x in range(4, 100, 12)],
        rect(0, 56, 100, 44, FLOOR),
        rect(6, 54, 88, 32, TEAL, r=2), rect(6, 52, 88, 4, GREY_DARK),
        rect(10, 60, 24, 22, TEAL_DARK, r=2), rect(38, 60, 24, 22, GREY_DARK, r=2), rect(66, 60, 24, 22, TEAL_DARK, r=2),
        rect(41, 64, 18, 12, "#FFB04A", op=0.5),
        rect(8, 6, 30, 18, TEAL_DARK, r=2), rect(62, 6, 30, 18, TEAL_DARK, r=2),
        path("M44 38 H 60 V 48 Q 60 52 56 52 H 48 Q 44 52 44 48 Z", RED), rect(42, 36, 20, 3, RED_DARK, r=1.5),
    ]


@art("room_bathroom", "home")
def bathroom() -> list[str]:
    return [
        rect(0, 0, 100, 60, TEAL_LIGHT), *[rect(0, y, 100, 0.8, WHITE) for y in range(10, 60, 10)],
        rect(0, 60, 100, 40, "#E4E8EC"),
        path("M6 54 H 66 V 64 Q 66 80 50 80 H 22 Q 6 80 6 64 Z", WHITE), rect(4, 52, 64, 5, GREY_LIGHT, r=2),
        rect(12, 80, 5, 6, GREY), rect(55, 80, 5, 6, GREY),
        stroke("M60 52 V 34 Q 60 30 56 30", GREY_DARK, 2.6),
        circle(18, 50, 5, WHITE), circle(26, 48, 6, WHITE), circle(34, 50, 5, WHITE),
        rect(74, 46, 18, 10, WHITE, r=3), path("M76 56 H 90 L 88 74 H 78 Z", WHITE), rect(74, 30, 18, 16, GREY_LIGHT, r=2),
        ellipse(46, 42, 7, 4, YELLOW), circle(41, 39, 3, YELLOW), poly("38.5,39 36,38 38.5,41", ORANGE),
    ]


@art("room_living_room", "home")
def living_room() -> list[str]:
    return [
        rect(0, 0, 100, 64, "#F4E1D2"), rect(0, 64, 100, 36, FLOOR),
        rect(22, 10, 40, 26, BLACK, r=2), rect(24, 12, 36, 22, "#4D6A8F"), rect(38, 36, 8, 6, GREY_DARK),
        rect(10, 46, 64, 22, RED, r=6), rect(4, 56, 12, 22, RED_DARK, r=5), rect(68, 56, 12, 22, RED_DARK, r=5),
        rect(14, 66, 56, 10, RED_DARK, r=3),
        rect(84, 54, 4, 30, BROWN_DARK), path("M76 40 H 96 L 92 54 H 80 Z", YELLOW),
        ellipse(42, 90, 30, 6, TEAL, op=0.6),
    ]


@art("room_bedroom", "home")
def bedroom() -> list[str]:
    return [
        rect(0, 0, 100, 62, "#E6E0F2"), rect(0, 62, 100, 38, FLOOR),
        *window(62, 10, 22, 22),
        rect(6, 40, 6, 44, BROWN_DARK, r=2), rect(10, 58, 54, 14, WHITE), rect(10, 70, 54, 6, BROWN),
        path("M26 54 H 60 Q 64 54 64 58 V 72 H 26 Z", "#8D6CC4"), rect(12, 50, 16, 10, OFF_WHITE, r=4),
        rect(70, 60, 20, 22, BROWN, r=2), rect(72, 64, 16, 6, BROWN_LIGHT, r=1),
        rect(76, 50, 3, 10, GREY_DARK), path("M71 44 H 85 L 82 50 H 74 Z", YELLOW),
    ]
