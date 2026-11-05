"""Food, drink and kitchenware (units 5.1, 5.2). The plain functions are reused as shop signs."""
from __future__ import annotations

from .kit import (
    BROWN, BROWN_DARK, BROWN_LIGHT, CREAM, GREEN, GREEN_DARK, GREEN_LIGHT, GREY, GREY_DARK, GREY_LIGHT, NAVY,
    OFF_WHITE, ORANGE, ORANGE_DARK, RED, RED_DARK, TEAL, WHITE, YELLOW, YELLOW_DARK, art, circle, ellipse, group,
    line, path, poly, rect, shadow, stroke,
)


def place(items: list[str], cx: float, cy: float, scale: float) -> str:
    """A full-canvas drawing shrunk around (cx, cy), for signs and small props."""
    return group(items, transform=f"translate({cx - 50 * scale} {cy - 50 * scale}) scale({scale})")


def bread() -> list[str]:
    return [
        shadow(50, 74, 34),
        path("M16 66 Q 10 52 26 44 L 70 30 Q 88 26 90 40 Q 92 50 80 56 L 34 72 Q 20 76 16 66 Z", BROWN_LIGHT),
        path("M20 66 Q 30 74 34 72 L 80 56 Q 88 52 90 44 Q 86 54 74 56 L 32 68 Q 24 70 20 66 Z", BROWN),
        *[stroke(f"M{34 + i * 13} {58 - i * 4.4} q 5 -6 10 -7", CREAM, 2.6) for i in range(4)],
    ]


def water() -> list[str]:
    return [
        shadow(50, 84, 18),
        path("M33 24 H 67 L 62 82 Q 61 86 57 86 H 43 Q 39 86 38 82 Z", "#EAF6FB"),
        path("M35.5 42 H 64.5 L 62 82 Q 61 86 57 86 H 43 Q 39 86 38 82 Z", "#8CCBEA"),
        path("M35.5 42 Q 50 47 64.5 42", "none", stroke="#BFE3F4", sw=2),
        rect(40, 30, 3, 46, WHITE, r=1.5, op=0.6),
        path("M76 22 Q 70 32 76 36 Q 82 32 76 22 Z", "#5DB4E0"),
    ]


def milk() -> list[str]:
    return [
        shadow(50, 88, 22),
        poly("30,34 50,24 70,34 70,88 30,88", WHITE),
        poly("50,24 70,34 70,88 50,88 50,34", GREY_LIGHT),
        poly("30,34 50,24 50,34 30,44", "#F0F2F5"),
        rect(40, 14, 10, 8, BLUE_CAP, r=1.5),
        rect(32, 52, 36, 20, TEAL, op=0.9),
        path("M36 68 Q 42 56 50 64 Q 56 56 64 68 Z", WHITE),
    ]


BLUE_CAP = "#3C7DD9"


def coffee() -> list[str]:
    return [
        ellipse(50, 80, 32, 7, OFF_WHITE), ellipse(50, 79, 24, 4.5, GREY_LIGHT),
        path("M28 46 H 72 V 60 Q 72 78 50 78 Q 28 78 28 60 Z", RED),
        path("M72 50 Q 84 50 84 58 Q 84 66 72 66", "none", stroke=RED, sw=5),
        ellipse(50, 46, 22, 5, BROWN_DARK),
        stroke("M42 38 q -4 -6 0 -12 q 4 -6 0 -12 M56 38 q -4 -6 0 -12 q 4 -6 0 -12", GREY, 2.6, op=0.7),
    ]


def tea() -> list[str]:
    return [
        ellipse(50, 80, 32, 7, OFF_WHITE),
        path("M28 50 H 72 V 60 Q 72 78 50 78 Q 28 78 28 60 Z", TEAL),
        path("M72 54 Q 82 54 82 61 Q 82 68 72 68", "none", stroke=TEAL, sw=4.5),
        ellipse(50, 50, 22, 4.5, ORANGE_DARK),
        stroke("M50 50 L 56 26", GREY_DARK, 1),
        rect(53, 18, 9, 9, YELLOW, r=1),
    ]


def wine() -> list[str]:
    return [
        shadow(50, 86, 16),
        path("M32 18 H 68 Q 70 46 50 54 Q 30 46 32 18 Z", "#F2F7FA", op=0.9),
        path("M32.6 30 H 67.4 Q 67 47 50 53 Q 33 47 32.6 30 Z", "#8E1B3A"),
        rect(48.5, 53, 3, 26, "#E3EDF2"),
        ellipse(50, 82, 14, 3.5, "#E3EDF2"),
        rect(38, 22, 3, 18, WHITE, r=1.5, op=0.7),
    ]


def beer() -> list[str]:
    return [
        shadow(50, 88, 22),
        path("M68 40 Q 82 40 82 52 Q 82 66 68 66", "none", stroke="#F2D28A", sw=5),
        rect(28, 32, 42, 54, YELLOW, r=4),
        rect(28, 32, 42, 54, "#FFFFFF", r=4, op=0.15),
        *[rect(34 + i * 10, 42, 3, 38, YELLOW_DARK, r=1.5, op=0.5) for i in range(3)],
        circle(32, 30, 7, WHITE), circle(42, 26, 8, WHITE), circle(54, 27, 8, WHITE), circle(64, 30, 7, WHITE),
        rect(28, 28, 42, 8, WHITE),
    ]


def juice() -> list[str]:
    return [
        shadow(50, 86, 18),
        path("M34 30 H 66 L 62 82 Q 61 86 57 86 H 43 Q 39 86 38 82 Z", "#FDEBD3"),
        path("M35 40 H 65 L 62 82 Q 61 86 57 86 H 43 Q 39 86 38 82 Z", ORANGE),
        line(56, 20, 52, 60, RED, 3),
        circle(68, 34, 11, ORANGE_DARK), circle(68, 34, 9, "#FFD08A"),
        *[line(68, 34, 68 + dx, 34 + dy, ORANGE_DARK, 1) for dx, dy in ((9, 0), (-9, 0), (0, 9), (0, -9))],
    ]


def meat() -> list[str]:
    return [
        shadow(50, 80, 32),
        path("M22 48 Q 20 28 44 26 Q 70 24 78 42 Q 86 60 66 70 Q 46 80 30 70 Q 22 62 22 48 Z", "#D9474E"),
        path("M28 50 Q 28 34 46 32 Q 66 30 72 44 Q 78 58 62 64 Q 46 72 34 64 Q 28 58 28 50 Z", "#B7323C"),
        path("M38 46 Q 46 40 54 46 Q 60 52 52 56 Q 42 58 38 46 Z", "#F6D5C8"),
        stroke("M60 40 q 6 4 4 12", "#F6D5C8", 2),
    ]


def fish() -> list[str]:
    return [
        shadow(50, 78, 30),
        path("M16 50 Q 36 26 62 34 Q 74 38 80 50 Q 74 62 62 66 Q 36 74 16 50 Z", "#7FB3C9"),
        path("M16 50 Q 36 74 62 66 Q 74 62 80 50 Q 60 60 40 58 Q 26 56 16 50 Z", "#5E93AB"),
        poly("78,50 92,36 90,50 92,64", "#5E93AB"),
        circle(30, 46, 3, WHITE), circle(30, 46, 1.6, NAVY),
        stroke("M42 40 q 4 10 0 20 M52 38 q 4 12 0 24", "#B9D8E5", 1.8),
    ]


def chicken() -> list[str]:
    return [
        ellipse(50, 74, 36, 8, OFF_WHITE),
        path("M24 62 Q 20 38 46 34 Q 72 32 76 52 Q 78 68 56 70 Q 30 72 24 62 Z", "#D98C3A"),
        path("M30 60 Q 30 46 46 42 Q 60 42 62 52", "none", stroke="#F0B56A", sw=3),
        rect(70, 40, 16, 6, "#E9A04D", r=3, transform="rotate(-30 70 40)"),
        circle(86, 32, 4, OFF_WHITE), circle(89, 36, 4, OFF_WHITE),
        rect(14, 50, 14, 6, "#E9A04D", r=3, transform="rotate(30 14 50)"),
    ]


def egg() -> list[str]:
    return [
        shadow(50, 82, 30),
        ellipse(36, 58, 16, 21, "#FBF4E8"), ellipse(36, 58, 16, 21, BROWN_LIGHT, op=0.25),
        path("M54 50 Q 50 72 62 80 Q 84 84 86 64 Q 88 44 72 42 Q 58 40 54 50 Z", WHITE),
        circle(69, 61, 9, YELLOW), circle(66, 58, 3, "#FFE58A"),
    ]


def potato() -> list[str]:
    return [
        shadow(50, 80, 30),
        path("M20 56 Q 18 36 42 32 Q 70 28 80 46 Q 88 64 64 72 Q 36 80 24 68 Q 20 64 20 56 Z", "#C9995E"),
        path("M24 66 Q 40 76 64 70 Q 82 64 82 52 Q 76 64 58 66 Q 36 68 24 66 Z", "#A97A44"),
        *[ellipse(x, y, 2, 1.4, "#8C6235") for x, y in ((38, 46), (58, 42), (66, 56), (44, 60))],
    ]


def tomato() -> list[str]:
    return [
        shadow(50, 84, 28),
        circle(50, 56, 28, RED),
        path("M26 66 Q 40 86 66 80 Q 80 72 78 56 Q 72 72 54 76 Q 36 78 26 66 Z", RED_DARK),
        ellipse(38, 46, 6, 3.5, WHITE, op=0.35, transform="rotate(-30 38 46)"),
        path("M50 30 L 40 26 L 46 34 L 36 38 L 48 37 L 50 44 L 52 37 L 64 38 L 54 34 L 60 26 Z", GREEN_DARK),
        stroke("M50 30 q 2 -8 6 -10", GREEN_DARK, 2.6),
    ]


def lettuce() -> list[str]:
    return [
        shadow(50, 86, 30),
        path("M18 60 Q 14 40 30 36 Q 34 22 50 24 Q 66 22 70 36 Q 86 40 82 60 Q 80 84 50 84 Q 20 84 18 60 Z",
             GREEN),
        path("M30 62 Q 28 44 42 40 Q 50 32 58 40 Q 72 44 70 62 Q 68 78 50 78 Q 32 78 30 62 Z", GREEN_LIGHT),
        stroke("M50 80 V 44 M50 64 L 40 52 M50 64 L 60 52 M50 72 L 38 64 M50 72 L 62 64", "#E6F4DA", 1.8),
    ]


def apple() -> list[str]:
    return [
        shadow(50, 86, 26),
        path("M50 34 Q 34 24 24 38 Q 14 56 28 76 Q 38 88 50 82 Q 62 88 72 76 Q 86 56 76 38 Q 66 24 50 34 Z", RED),
        path("M50 82 Q 62 88 72 76 Q 86 56 76 38 Q 80 60 68 72 Q 58 80 50 82 Z", RED_DARK),
        ellipse(35, 46, 5, 8, WHITE, op=0.3, transform="rotate(20 35 46)"),
        stroke("M50 34 Q 50 24 54 18", BROWN_DARK, 3),
        path("M54 24 Q 66 14 74 22 Q 64 30 54 24 Z", GREEN),
    ]


def orange() -> list[str]:
    return [
        shadow(50, 86, 26),
        circle(50, 56, 28, ORANGE),
        path("M26 66 Q 40 86 66 80 Q 80 72 78 56 Q 72 72 54 76 Q 36 78 26 66 Z", ORANGE_DARK),
        *[circle(x, y, 0.9, ORANGE_DARK) for x, y in ((40, 48), (52, 44), (60, 54), (46, 60), (36, 58), (56, 66))],
        path("M50 28 Q 58 16 70 20 Q 62 32 50 28 Z", GREEN),
    ]


def cheese() -> list[str]:
    return [
        shadow(50, 80, 32),
        poly("18,52 82,38 82,74 18,74", YELLOW),
        poly("18,52 82,38 74,30", "#FFE07A"),
        poly("82,38 82,74 74,70 74,30", YELLOW_DARK),
        circle(36, 62, 5, YELLOW_DARK), circle(56, 56, 3.5, YELLOW_DARK), circle(64, 66, 4, YELLOW_DARK),
        circle(46, 70, 2.5, YELLOW_DARK),
    ]


def oil() -> list[str]:
    return [
        shadow(50, 88, 18),
        rect(44, 12, 12, 8, NAVY, r=1.5),
        path("M44 20 H 56 V 30 Q 66 36 66 48 V 84 Q 66 88 62 88 H 38 Q 34 88 34 84 V 48 Q 34 36 44 30 Z",
             "#C2B33A"),
        path("M56 30 Q 66 36 66 48 V 84 Q 66 88 62 88 H 54 V 30 Z", "#A39629"),
        rect(38, 54, 24, 18, OFF_WHITE, r=2),
        ellipse(50, 63, 5, 3, GREEN, transform="rotate(-30 50 63)"),
    ]


def salt() -> list[str]:
    return [
        shadow(50, 88, 18),
        path("M36 40 Q 36 30 50 30 Q 64 30 64 40 Z", GREY),
        *[circle(46 + dx, 35, 1.2, GREY_DARK) for dx in (0, 4, 8)],
        path("M36 40 H 64 L 66 84 Q 66 88 62 88 H 38 Q 34 88 34 84 Z", "#F1F4F7"),
        path("M36 40 H 64 L 66 84 Q 66 88 62 88 H 56 Z", GREY_LIGHT),
        *[circle(x, y, 1, WHITE) for x, y in ((26, 30), (22, 40), (30, 20))],
    ]


def sugar() -> list[str]:
    cube = lambda x, y: [poly(f"{x},{y} {x + 12},{y - 5} {x + 24},{y} {x + 12},{y + 5}", WHITE),  # noqa: E731
                         poly(f"{x},{y} {x + 12},{y + 5} {x + 12},{y + 19} {x},{y + 14}", "#D5DCE4"),
                         poly(f"{x + 12},{y + 5} {x + 24},{y} {x + 24},{y + 14} {x + 12},{y + 19}", "#B4BEC9")]
    return [shadow(50, 82, 30), *cube(22, 62), *cube(50, 62), *cube(36, 42)]


def pot() -> list[str]:
    return [
        shadow(50, 84, 32),
        rect(14, 44, 10, 6, GREY_DARK, r=3), rect(76, 44, 10, 6, GREY_DARK, r=3),
        path("M22 40 H 78 V 74 Q 78 84 68 84 H 32 Q 22 84 22 74 Z", GREY),
        path("M62 40 H 78 V 74 Q 78 84 68 84 H 62 Z", GREY_DARK, op=0.5),
        rect(18, 34, 64, 8, GREY_DARK, r=4), rect(44, 26, 12, 8, NAVY, r=3),
        stroke("M38 22 q -4 -6 0 -12 M62 22 q -4 -6 0 -12", GREY, 2.4, op=0.6),
    ]


def frying_pan() -> list[str]:
    return [
        shadow(42, 80, 30),
        ellipse(42, 60, 30, 18, BLACK_PAN), ellipse(42, 57, 25, 13, "#4A4F57"),
        rect(68, 50, 30, 8, BROWN_DARK, r=4, transform="rotate(-20 68 50)"),
        ellipse(36, 54, 7, 4, YELLOW), ellipse(48, 60, 6, 3.5, WHITE, op=0.9), circle(48, 60, 2.4, YELLOW),
    ]


BLACK_PAN = "#2F3238"


def plate() -> list[str]:
    return [
        shadow(50, 80, 36),
        ellipse(50, 64, 38, 18, WHITE), ellipse(50, 63, 26, 11, "#EEF1F4"),
        rect(6, 40, 4, 36, GREY, r=2), *[rect(4 + i * 2.6, 32, 1.4, 10, GREY, r=0.7) for i in range(3)],
        path("M90 40 Q 96 52 92 60 V 76 Q 92 78 90 78 Q 88 78 88 76 V 40 Z", GREY),
    ]


for image_id, background, draw in (
    ("food_bread", "food", bread), ("drink_water", "drink", water), ("drink_milk", "drink", milk),
    ("drink_coffee", "drink", coffee), ("drink_tea", "drink", tea), ("drink_wine", "drink", wine),
    ("drink_beer", "drink", beer), ("drink_juice", "drink", juice), ("food_meat", "food", meat),
    ("food_fish", "food", fish), ("food_chicken", "food", chicken), ("food_egg", "food", egg),
    ("food_potato", "food", potato), ("food_tomato", "food", tomato), ("food_lettuce", "food", lettuce),
    ("food_apple", "food", apple), ("food_orange", "food", orange), ("food_cheese", "food", cheese),
    ("food_oil", "kitchen", oil), ("food_salt", "kitchen", salt), ("food_sugar", "kitchen", sugar),
    ("kitchen_pot", "kitchen", pot), ("kitchen_frying_pan", "kitchen", frying_pan),
    ("kitchen_plate", "kitchen", plate),
):
    art(image_id, background)(draw)

