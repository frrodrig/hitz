"""Body parts and health (units 8.1, 8.2)."""
from __future__ import annotations

from .kit import (
    BLUE, GREEN, GREEN_DARK, GREY, GREY_DARK, GREY_LIGHT, HAIR_BROWN, NAVY, OFF_WHITE, PINK, RED, RED_DARK, SKIN,
    SKIN_SHADE, TEAL, TEAL_LIGHT, WHITE, YELLOW, art, circle, ellipse, line, path, person, rect, shadow, stroke,
)

LIPS = "#D9636E"
LIPS_DARK = "#B8434F"


@art("body_head", "body")
def head() -> list[str]:
    return person(top=14, scale=1.25, hair_color=HAIR_BROWN, shirt=TEAL, shirt_shade="#3D7F8B", body_bottom=100)


@art("body_eye", "body")
def eye() -> list[str]:
    return [
        path("M12 52 Q 50 12 88 52 Q 50 88 12 52 Z", WHITE),
        circle(50, 52, 17, "#6B8E4E"), circle(50, 52, 8, NAVY), circle(44, 46, 4, WHITE),
        stroke("M12 52 Q 50 12 88 52", SKIN_SHADE, 4),
        *[stroke(f"M{x} {y} l {dx} {dy}", NAVY, 2.4) for x, y, dx, dy in
          ((24, 38, -5, -7), (37, 30, -2, -8), (50, 27, 0, -8), (63, 30, 2, -8), (76, 38, 5, -7))],
        stroke("M24 22 Q 50 6 78 20", HAIR_BROWN, 5),
    ]


@art("body_ear", "body")
def ear() -> list[str]:
    return [
        path("M40 14 Q 74 10 76 42 Q 78 62 62 72 Q 56 76 56 86 Q 54 94 44 92 Q 32 90 34 78", SKIN),
        path("M40 14 Q 74 10 76 42 Q 78 62 62 72 Q 56 76 56 86 Q 54 94 44 92 Q 32 90 34 78 Q 30 50 30 36 Q 30 16 40 14 Z", SKIN),
        stroke("M44 28 Q 62 24 64 42 Q 64 54 54 60 Q 48 64 50 72", SKIN_SHADE, 4),
        stroke("M46 42 Q 52 40 52 48", SKIN_SHADE, 3),
    ]


@art("body_nose", "body")
def nose() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKIN), circle(28, 30, 4, "#1E2A44"), circle(72, 30, 4, "#1E2A44"),
        path("M50 22 Q 46 50 34 64 Q 30 72 40 74 Q 46 76 50 72 Q 54 76 60 74 Q 70 72 66 64 Q 54 50 50 22 Z", SKIN_SHADE),
        ellipse(42, 68, 3.5, 2.2, "#9A6140"), ellipse(58, 68, 3.5, 2.2, "#9A6140"),
        path("M50 26 Q 48 48 42 60", "none", stroke="#F6D3B2", sw=3),
    ]


@art("body_mouth", "body")
def mouth() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKIN),
        path("M14 50 Q 30 30 42 36 Q 50 40 58 36 Q 70 30 86 50 Q 70 54 50 54 Q 30 54 14 50 Z", LIPS),
        path("M14 50 Q 30 54 50 54 Q 70 54 86 50 Q 70 78 50 78 Q 30 78 14 50 Z", LIPS_DARK),
        path("M22 51 Q 50 60 78 51 Q 50 56 22 51 Z", "#7A2C2C"),
        ellipse(40, 64, 8, 3, WHITE, op=0.3),
    ]


@art("body_tooth", "body")
def tooth() -> list[str]:
    return [
        shadow(50, 90, 22),
        path("M26 30 Q 26 14 40 16 Q 50 20 60 16 Q 74 14 74 30 Q 74 46 68 56 Q 64 86 58 86 Q 52 86 52 70 Q 50 62 48 70 "
             "Q 48 86 42 86 Q 36 86 32 56 Q 26 46 26 30 Z", WHITE),
        path("M34 24 Q 36 20 42 22", "none", stroke=TEAL_LIGHT, sw=3),
        circle(40, 42, 2.4, NAVY), circle(60, 42, 2.4, NAVY), stroke("M44 50 Q 50 55 56 50", NAVY, 2),
        circle(80, 20, 3, YELLOW), path("M84 30 l 1.5 3 l 3 1.5 l -3 1.5 l -1.5 3 l -1.5 -3 l -3 -1.5 l 3 -1.5 z", YELLOW),
    ]


@art("body_hand", "body")
def hand() -> list[str]:
    fingers = [(30, 18, 34), (42, 10, 40), (54, 12, 38), (66, 20, 32)]
    return [
        *[rect(x - 5, y, 10, h + 20, SKIN, r=5) for x, y, h in fingers],
        path("M24 48 H 72 V 74 Q 72 92 52 92 Q 30 92 26 76 Z", SKIN),
        path("M24 56 Q 10 46 12 40 Q 16 34 26 44 L 32 52 Z", SKIN),
        stroke("M36 74 Q 48 80 60 74", SKIN_SHADE, 2.4),
        *[ellipse(x, y + 5, 3.2, 2.4, "#F6DCC4") for x, y, _ in fingers],
    ]


@art("body_foot", "body")
def foot() -> list[str]:
    return [
        shadow(50, 86, 38),
        path("M24 20 Q 38 18 40 32 V 56 Q 42 70 62 70 H 84 Q 92 70 92 78 Q 92 86 80 86 H 26 Q 16 86 16 74 V 28 "
             "Q 16 20 24 20 Z", SKIN),
        *[circle(x, 72 - abs(x - 70) * 0.05, r, SKIN) for x, r in ((88, 5), (80, 4.4), (73, 4), (66, 3.6))],
        stroke("M26 70 Q 34 78 46 80", SKIN_SHADE, 2.4),
        *[ellipse(x, 68.5, 2, 1.4, "#F6DCC4") for x in (88, 80, 73, 66)],
    ]


@art("body_arm", "body")
def arm() -> list[str]:
    return [
        path("M6 72 Q 20 60 40 62 L 60 64 Q 66 56 62 40 L 58 26 Q 56 18 64 16 Q 72 16 74 24 L 80 46 Q 86 72 66 82 "
             "Q 40 92 6 88 Z", SKIN),
        path("M58 26 Q 56 18 64 16 Q 72 16 74 24 Q 68 20 62 22 Z", SKIN_SHADE),
        path("M36 62 Q 48 46 62 60", "none", stroke=SKIN_SHADE, sw=3),
        rect(0, 70, 12, 20, TEAL),
    ]


@art("body_leg", "body")
def leg() -> list[str]:
    return [
        rect(34, 0, 32, 26, BLUE, r=2), rect(34, 0, 32, 4, "#285BA6"),
        path("M38 26 H 62 L 60 54 Q 58 60 60 70 L 60 80 H 42 L 42 70 Q 40 60 40 54 Z", SKIN),
        ellipse(50, 54, 9, 5, SKIN_SHADE, op=0.6),
        path("M40 78 H 62 Q 64 88 82 88 Q 88 90 86 94 H 40 Z", RED), rect(40, 92, 48, 4, RED_DARK, r=1),
    ]


@art("health_medicine", "body")
def medicine() -> list[str]:
    return [
        shadow(50, 88, 22),
        rect(34, 14, 32, 12, WHITE, r=3), rect(34, 22, 32, 4, GREY_LIGHT),
        rect(30, 26, 40, 62, "#E8873A", r=6, op=0.95), rect(30, 26, 40, 62, WHITE, r=6, op=0.15),
        rect(34, 44, 32, 26, WHITE, r=2), rect(46, 49, 8, 16, GREEN, r=1), rect(42, 53, 16, 8, GREEN, r=1),
    ]


@art("health_pills", "body")
def pills() -> list[str]:
    return [
        shadow(50, 84, 32),
        rect(14, 50, 40, 16, WHITE, r=8, transform="rotate(-25 34 58)"),
        rect(34, 50, 20, 16, RED, r=8, transform="rotate(-25 34 58)"), rect(34, 50, 10, 16, RED, transform="rotate(-25 34 58)"),
        circle(68, 66, 12, OFF_WHITE), line(60, 66, 76, 66, GREY, 2),
        circle(72, 36, 9, "#7FB8E6"), circle(72, 36, 9, WHITE, op=0.15),
    ]


@art("health_plant", "nature")
def medicinal_plant() -> list[str]:
    return [
        shadow(50, 88, 22),
        path("M32 66 H 68 L 64 88 H 36 Z", "#C46A3A"), rect(28, 62, 44, 6, "#A85A30", r=2),
        stroke("M50 62 V 24", GREEN_DARK, 3),
        *[path(f"M50 {y} Q {50 + d * 22} {y - 10} {50 + d * 24} {y - 2} Q {50 + d * 14} {y + 6} 50 {y} Z", GREEN)
          for y, d in ((54, -1), (46, 1), (38, -1), (30, 1))],
        *[circle(x, y, 2.6, "#B8A0E6") for x, y in ((50, 22), (46, 26), (54, 26))],
    ]


@art("health_cough", "body")
def cough() -> list[str]:
    return [
        *person(hair_color=HAIR_BROWN, shirt=GREY, shirt_shade=GREY_DARK, smile=False,
                extras_front=[path("M44 40 Q 50 36 56 40 Q 50 46 44 40 Z", "#7A2C2C"),
                              circle(55, 45, 6.5, "#E9B488"), rect(50, 50, 10, 14, "#E9B488", r=4)]),
        *[stroke(f"M{66 + i * 6} {40 - i * 2} q 4 -2 0 -6", BLUE, 2) for i in range(3)],
        circle(84, 30, 2, BLUE, op=0.6), circle(88, 40, 1.6, BLUE, op=0.6),
    ]


@art("health_fever", "body")
def fever() -> list[str]:
    therm = [rect(58, 42, 5, 22, WHITE, r=2.5, transform="rotate(30 60 50)"),
             rect(59, 46, 3, 16, RED, r=1.5, transform="rotate(30 60 50)")]
    drops = [path("M30 22 Q 26 30 30 32 Q 34 30 30 22 Z", "#7FC1E8"), path("M72 18 Q 68 26 72 28 Q 76 26 72 18 Z", "#7FC1E8")]
    cheeks = [circle(36, 40, 4, RED, op=0.5), circle(64, 40, 4, RED, op=0.5)]
    return [*person(hair_color=HAIR_BROWN, shirt=PINK, shirt_shade="#D98597", smile=False,
                    extras_front=[*cheeks, *therm]), *drops]
