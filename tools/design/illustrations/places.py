"""Places in town and nature, shops and services (units 2.1, 6.1, 7.2, 10.1, 11.1)."""
from __future__ import annotations

from .food import apple, beer, bread, fish, meat, place, plate
from .kit import (
    BLACK, BLUE, BLUE_DARK, BROWN, BROWN_DARK, BROWN_LIGHT, CREAM, GREEN, GREEN_DARK, GREEN_LIGHT, GREY, GREY_DARK,
    GREY_LIGHT, NAVY, NAVY_LIGHT, OFF_WHITE, ORANGE, PINK, PURPLE, RED, RED_DARK, SKY, TEAL, TEAL_DARK, TEAL_LIGHT,
    WHITE, YELLOW, YELLOW_DARK, art, circle, door, ellipse, ground, line, path, poly, rect, stroke, storefront,
    window,
)


def sky_and_ground(grass: str = GREEN_LIGHT, y: float = 86) -> list[str]:
    return [rect(0, 0, 100, 100, SKY), ground(grass, y)]


def columns(x: float, y: float, w: float, h: float, count: int, color: str = OFF_WHITE) -> list[str]:
    gap = w / count
    return [rect(x + i * gap + gap * 0.25, y, gap * 0.5, h, color) for i in range(count)]


def classic(sign: list[str], roof: str = GREY_LIGHT, wall: str = OFF_WHITE) -> list[str]:
    """A building with a pediment and columns; the sign in the pediment says what it is."""
    return [
        *sky_and_ground(),
        poly("12,36 50,14 88,36", roof), rect(12, 36, 76, 6, roof),
        rect(16, 42, 68, 38, wall), *columns(16, 44, 68, 36, 5, "#E3E6EA"),
        rect(10, 80, 80, 4, roof), rect(6, 84, 88, 4, GREY),
        *sign,
    ]


def cross(cx: float, cy: float, size: float, color: str) -> list[str]:
    t = size / 3
    return [rect(cx - t / 2, cy - size / 2, t, size, color, r=t * 0.2), rect(cx - size / 2, cy - t / 2, size, t, color, r=t * 0.2)]


@art("place_beach", "place")
def beach() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKY), circle(80, 18, 8, YELLOW),
        rect(0, 44, 100, 24, "#4BA3D1"), path("M0 50 Q 12 46 24 50 T 48 50 T 72 50 T 100 50", "none", stroke=WHITE, sw=1.6, op=0.7),
        path("M0 64 Q 40 56 100 62 V 100 H 0 Z", "#F3D9A4"),
        line(34, 40, 42, 84, BROWN_DARK, 2),
        path("M14 44 Q 30 22 54 34 Q 36 34 14 44 Z", RED), path("M24 39 Q 32 30 44 32 L 34 38 Z", WHITE),
        rect(52, 74, 30, 8, TEAL, r=2, transform="rotate(-6 52 74)"),
        circle(70, 88, 6, YELLOW), path("M64 88 A 6 6 0 0 1 76 88 Z", RED),
    ]


@art("place_mountain", "nature")
def mountain() -> list[str]:
    return [
        rect(0, 0, 100, 100, SKY), circle(22, 20, 7, YELLOW),
        poly("0,86 36,30 72,86", GREEN_DARK), poly("36,30 46,46 40,44 34,48 28,42", WHITE),
        poly("30,86 66,20 100,80 100,86", GREEN), poly("66,20 78,40 72,38 66,44 58,36", WHITE),
        ground(GREEN_LIGHT, 88),
    ]


@art("place_park", "nature")
def park() -> list[str]:
    return [
        *sky_and_ground(GREEN_LIGHT, 64), rect(0, 64, 100, 36, GREEN_LIGHT),
        path("M44 100 Q 50 80 70 64 H 78 Q 60 82 58 100 Z", "#E8D7B5"),
        rect(22, 40, 5, 30, BROWN_DARK), circle(24.5, 34, 16, GREEN), circle(16, 40, 9, GREEN_DARK, op=0.4),
        rect(70, 46, 4, 18, BROWN_DARK), circle(72, 42, 10, GREEN_DARK),
        rect(30, 74, 30, 4, BROWN, r=1), rect(30, 68, 30, 3, BROWN, r=1), rect(32, 78, 3, 8, GREY_DARK), rect(55, 78, 3, 8, GREY_DARK),
        circle(84, 76, 2, PINK), circle(88, 80, 2, YELLOW), circle(12, 82, 2, PINK),
    ]


@art("place_church", "place")
def church() -> list[str]:
    return [
        *sky_and_ground(),
        rect(20, 50, 40, 36, OFF_WHITE), poly("16,52 40,34 64,52", BROWN_DARK),
        rect(56, 30, 22, 56, "#EFE7DA"), poly("54,32 67,8 80,32", BROWN_DARK),
        path("M62 50 V 44 A 5 5 0 0 1 72 44 V 50 Z", NAVY), circle(67, 46, 2.4, YELLOW),
        *door(32, 64, 14, 22, BROWN), circle(40, 52, 4, "#9BC3E6"),
        rect(66, 2, 2, 8, NAVY), rect(63.5, 4, 7, 2, NAVY),
    ]


@art("place_hospital", "place")
def hospital() -> list[str]:
    return [
        *sky_and_ground(),
        rect(14, 30, 72, 56, WHITE), rect(14, 30, 72, 6, GREY_LIGHT),
        rect(38, 14, 24, 22, RED, r=3), *cross(50, 25, 14, WHITE),
        *[part for x in (20, 34, 58, 72) for y in (42, 58) for part in window(x, y, 9, 10)],
        rect(40, 66, 20, 20, "#9BC3E6"), rect(49.5, 66, 1, 20, WHITE),
        rect(36, 62, 28, 4, RED),
    ]


@art("place_pharmacy", "place")
def pharmacy() -> list[str]:
    return [
        *sky_and_ground(),
        rect(14, 30, 72, 56, OFF_WHITE),
        rect(64, 14, 22, 22, GREEN, r=3), *cross(75, 25, 14, WHITE),
        rect(20, 52, 30, 24, "#9BC3E6", r=1.5),
        *[rect(23 + i * 9, 58, 5, 9, c, r=1.5) for i, c in enumerate((RED, WHITE, YELLOW))],
        *door(56, 54, 20, 32, GREEN_DARK),
        rect(14, 40, 72, 6, GREEN),
    ]


@art("place_station", "transport")
def station() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#E2E9F5"),
        poly("4,30 50,10 96,30", GREY_DARK), rect(8, 30, 6, 52, GREY), rect(86, 30, 6, 52, GREY),
        circle(50, 26, 7, WHITE), circle(50, 26, 7, "none", stroke=NAVY, sw=1.4),
        line(50, 26, 50, 21, NAVY, 1.4), line(50, 26, 54, 26, NAVY, 1.4),
        rect(14, 46, 72, 30, RED, r=6), rect(14, 46, 72, 8, RED_DARK, r=4),
        *[rect(20 + i * 17, 56, 12, 10, SKY, r=1.5) for i in range(4)],
        circle(28, 78, 5, BLACK), circle(72, 78, 5, BLACK),
        rect(0, 82, 100, 4, GREY_DARK), rect(0, 86, 100, 14, GREY_LIGHT),
    ]


@art("place_hotel", "place")
def hotel() -> list[str]:
    stars = [path(f"M{x} 12 l 1.8 3.6 l 4 .6 l -2.9 2.8 l .7 4 l -3.6 -1.9 l -3.6 1.9 l .7 -4 l -2.9 -2.8 l 4 -.6 z", YELLOW)
             for x in (36, 46, 56, 66)]
    return [
        *sky_and_ground(), *stars,
        rect(24, 26, 52, 62, NAVY_LIGHT),
        *[part for x in (30, 45.5, 61) for y in (32, 46, 60) for part in window(x, y, 9, 9, glass=YELLOW)],
        rect(40, 74, 20, 14, "#9BC3E6"), path("M36 74 H 64 L 60 68 H 40 Z", RED),
    ]


@art("place_school", "place")
def school() -> list[str]:
    return [
        *sky_and_ground(),
        rect(12, 42, 76, 44, ORANGE), poly("8,44 50,22 92,44", RED_DARK),
        circle(50, 36, 6, WHITE), line(50, 36, 50, 32, NAVY, 1.2), line(50, 36, 53, 36, NAVY, 1.2),
        *window(18, 50, 12, 12), *window(70, 50, 12, 12), *door(42, 60, 16, 26, BLUE_DARK),
        # A ball and a little backpack by the door.
        circle(26, 82, 5, WHITE), path("M21 82 Q 26 78 31 82", "none", stroke=RED, sw=1.4),
        rect(66, 74, 12, 12, TEAL, r=3), rect(68, 78, 8, 4, TEAL_DARK, r=1),
    ]


@art("place_library", "place")
def library() -> list[str]:
    book = [rect(32, 18, 18, 14, RED, r=1), rect(50, 18, 18, 14, BLUE, r=1), rect(49, 18, 2, 14, NAVY)]
    return classic(book, roof=BROWN_LIGHT, wall=CREAM)


@art("place_bank", "place")
def bank() -> list[str]:
    coin = [circle(50, 26, 8, YELLOW), circle(50, 26, 5.5, YELLOW_DARK), rect(48.5, 22, 3, 8, YELLOW, r=1)]
    return classic(coin)


@art("place_museum", "place")
def museum() -> list[str]:
    painting = [rect(40, 18, 20, 14, BROWN, r=1), rect(42, 20, 16, 10, SKY), poly("42,30 48,23 52,27 54,25 58,30", GREEN),
                circle(54, 23, 1.6, YELLOW)]
    return classic(painting, roof="#D9CFC0")


@art("place_restaurant", "place")
def restaurant() -> list[str]:
    sign = [ellipse(50, 26, 7, 4, WHITE), rect(38, 21, 1.6, 10, WHITE, r=0.8), rect(61, 21, 1.6, 10, WHITE, r=0.8)]
    return [*storefront(RED, WHITE), *sign, place(plate(), 37, 66, 0.34)]


@art("place_bar", "place")
def bar() -> list[str]:
    sign = [rect(44, 21, 9, 10, YELLOW, r=1), rect(44, 20, 9, 3, WHITE, r=1), path("M53 23 q 4 0 4 3 q 0 3 -4 3", "none", stroke=YELLOW, sw=1.4)]
    return [*storefront(GREEN_DARK, GREEN_LIGHT), *sign, place(beer(), 37, 65, 0.32)]


@art("place_city", "place")
def city() -> list[str]:
    towers = [(6, 40, 18, BLUE_DARK), (22, 24, 16, NAVY_LIGHT), (38, 46, 14, TEAL_DARK), (52, 14, 18, NAVY),
              (70, 34, 14, BLUE), (84, 50, 14, NAVY_LIGHT)]
    out = [rect(0, 0, 100, 100, SKY)]
    for x, y, w, color in towers:
        out.append(rect(x, y, w, 90 - y, color))
        out += [rect(x + 3 + c * (w - 6) / 2, y + 4 + r * 7, (w - 6) / 2 - 2, 4, YELLOW, op=0.7)
                for r in range(int((84 - y) / 7)) for c in range(2)]
    return [*out, rect(0, 88, 100, 12, GREY_DARK), *[rect(8 + i * 18, 93, 8, 1.6, WHITE) for i in range(6)]]


@art("place_town", "nature")
def town() -> list[str]:
    def little_house(x: float, y: float, roof: str) -> list[str]:
        return [rect(x, y, 16, 14, OFF_WHITE), poly(f"{x - 2},{y} {x + 8},{y - 8} {x + 18},{y}", roof),
                rect(x + 6, y + 6, 4, 8, BROWN)]
    return [
        rect(0, 0, 100, 100, SKY),
        path("M0 60 Q 30 30 60 44 T 100 40 V 100 H 0 Z", GREEN),
        path("M0 76 Q 40 60 100 72 V 100 H 0 Z", GREEN_LIGHT),
        rect(42, 26, 8, 22, "#EFE7DA"), poly("40,27 46,16 52,27", BROWN_DARK),
        *little_house(14, 50, RED), *little_house(56, 50, RED_DARK), *little_house(34, 66, RED),
        *little_house(74, 68, RED_DARK),
    ]


@art("shop_bakery", "place")
def bakery() -> list[str]:
    return [*storefront(ORANGE, CREAM), place(bread(), 50, 26, 0.16), place(bread(), 37, 66, 0.34)]


@art("shop_butcher", "place")
def butcher() -> list[str]:
    return [*storefront(RED, WHITE), place(meat(), 50, 26, 0.17), place(meat(), 37, 66, 0.34)]


@art("shop_fishmonger", "place")
def fishmonger() -> list[str]:
    return [*storefront(BLUE, WHITE), place(fish(), 50, 26, 0.17), place(fish(), 37, 66, 0.34)]


@art("shop_greengrocer", "place")
def greengrocer() -> list[str]:
    crates = [rect(18, 78, 16, 8, BROWN), *[circle(21 + i * 5, 77, 2.6, (RED, ORANGE, GREEN)[i]) for i in range(3)]]
    return [*storefront(GREEN, GREEN_LIGHT), place(apple(), 50, 26, 0.17), place(apple(), 37, 65, 0.3), *crates]


@art("shop_supermarket", "place")
def supermarket() -> list[str]:
    cart = [stroke("M36 20 H 40 L 44 30 H 60 L 63 23 H 42", WHITE, 1.8), circle(46, 33, 1.6, WHITE), circle(58, 33, 1.6, WHITE)]
    return [
        *sky_and_ground(), rect(6, 18, 88, 70, OFF_WHITE), rect(6, 18, 88, 20, RED), *cart,
        rect(12, 44, 76, 30, "#9BC3E6"), *[rect(12 + i * 19, 44, 1.5, 30, WHITE) for i in range(1, 4)],
        rect(40, 60, 20, 28, "#7FAFD8"), rect(49.5, 60, 1, 28, WHITE),
    ]


@art("shop_bookshop", "place")
def bookshop() -> list[str]:
    books = [rect(24 + i * 5, 60 + (i % 2) * 3, 4, 16 - (i % 2) * 3, c, r=0.6) for i, c in enumerate((RED, BLUE, YELLOW, GREEN, PURPLE))]
    sign = [rect(42, 21, 8, 10, RED, r=1), rect(50, 21, 8, 10, BLUE, r=1), rect(49.4, 21, 1.2, 10, NAVY)]
    return [*storefront(PURPLE, "#C9B6E8"), *books, *sign]


@art("shop_hairdresser", "place")
def hairdresser() -> list[str]:
    scissors = [circle(45, 30, 2.6, "none", stroke=WHITE, sw=1.4), circle(55, 30, 2.6, "none", stroke=WHITE, sw=1.4),
                line(46.5, 28, 55, 20, WHITE, 1.4), line(53.5, 28, 45, 20, WHITE, 1.4)]
    pole = [rect(84, 50, 6, 28, WHITE, r=3), *[rect(84, 52 + i * 7, 6, 3, RED) for i in range(4)]]
    big = [circle(31, 70, 4, "none", stroke=NAVY, sw=2), circle(43, 70, 4, "none", stroke=NAVY, sw=2),
           line(33.5, 67, 45, 57, GREY_DARK, 2.2), line(40.5, 67, 29, 57, GREY_DARK, 2.2)]
    return [*storefront(PINK, WHITE), *scissors, *pole, *big]


@art("shop_travel_agency", "place")
def travel_agency() -> list[str]:
    plane = [path("M38 27 L 62 24 L 64 26 L 40 30 Z", WHITE), poly("48,26 54,18 57,18 53,25", WHITE), poly("48,28 52,33 55,33 54,27", WHITE)]
    globe = [circle(37, 64, 9, BLUE), path("M31 60 q 4 -3 7 0 q 2 4 -2 6 q -4 0 -5 -6 z M40 66 q 4 -2 5 2 q -2 4 -5 2 z", GREEN)]
    return [*storefront(TEAL, TEAL_LIGHT), *plane, *globe]


@art("place_cinema", "leisure")
def cinema() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#2A2F45"),
        rect(10, 10, 80, 44, "#DDE6F5", r=2), poly("28,48 44,26 54,38 60,32 74,48", "#9FB5D8"), circle(64, 22, 5, YELLOW, op=0.8),
        *[rect(8 + i * 17, 66 + (i % 2) * 0, 14, 12, RED_DARK, r=3) for i in range(5)],
        *[rect(0 + i * 17, 82, 14, 14, RED, r=3) for i in range(6)],
        path("M80 64 L 84 84 H 96 L 100 64 Z", WHITE), *[circle(83 + i * 4.5, 62, 3, CREAM) for i in range(4)],
        *[rect(82 + i * 4, 64, 2, 20, RED) for i in range(4)],
    ]


@art("place_theatre", "leisure")
def theatre() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#3A2234"),
        rect(10, 18, 80, 56, "#4E3A55"), ellipse(50, 70, 22, 4, YELLOW, op=0.35),
        path("M6 10 H 46 Q 30 40 34 76 H 6 Z", RED), path("M94 10 H 54 Q 70 40 66 76 H 94 Z", RED),
        path("M6 10 H 46 Q 40 18 30 20 Q 14 20 6 16 Z", RED_DARK), path("M94 10 H 54 Q 60 18 70 20 Q 86 20 94 16 Z", RED_DARK),
        rect(0, 74, 100, 8, BROWN), rect(0, 82, 100, 18, BROWN_DARK),
        rect(0, 4, 100, 8, YELLOW_DARK),
    ]


@art("place_pool", "leisure")
def pool() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#DCEFF2"),
        rect(8, 24, 84, 66, "#4BB3D6", r=4), rect(8, 24, 84, 6, "#79CBE6", r=3),
        *[stroke(f"M8 {44 + i * 16} H 92", "#F3F3F3", 1.6, **{"stroke-dasharray": "4 3"}) for i in range(3)],
        *[path(f"M{14 + i * 18} {38 + (i % 2) * 20} q 4 -3 8 0 t 8 0", "none", stroke=WHITE, sw=1.4, op=0.6) for i in range(4)],
        stroke("M74 8 V 34 M86 8 V 34 M74 16 H 86 M74 24 H 86", GREY, 2.4),
    ]


@art("place_sports_centre", "leisure")
def sports_centre() -> list[str]:
    ball = [circle(50, 26, 9, ORANGE), stroke("M41 26 H 59 M50 17 V 35 M43.6 19.6 Q 49 26 43.6 32.4 M56.4 19.6 Q 51 26 56.4 32.4", BROWN_DARK, 1.2)]
    return [
        *sky_and_ground(),
        path("M8 86 V 44 Q 50 6 92 44 V 86 Z", TEAL), path("M8 44 Q 50 6 92 44 Q 50 18 8 44 Z", TEAL_DARK),
        rect(20, 50, 60, 12, "#9BC3E6"), *[rect(20 + i * 12, 50, 1.2, 12, WHITE) for i in range(1, 5)],
        rect(40, 66, 20, 20, "#7FAFD8"), rect(49.5, 66, 1, 20, WHITE), *ball,
    ]


@art("place_car_park", "transport")
def car_park() -> list[str]:
    return [
        rect(0, 0, 100, 100, "#C9CDD3"),
        *[rect(8 + i * 22, 56, 2, 40, WHITE) for i in range(5)],
        rect(14, 66, 12, 24, RED, r=4), rect(16, 70, 8, 6, SKY, r=1.5),
        rect(58, 66, 12, 24, BLUE, r=4), rect(60, 70, 8, 6, SKY, r=1.5),
        rect(34, 8, 32, 34, BLUE_DARK, r=6),
        path("M44 36 V 14 H 52 Q 60 14 60 21 Q 60 28 52 28 H 49", "none", stroke=WHITE, sw=5, join="round"),
        rect(48, 42, 4, 12, GREY_DARK),
    ]


@art("place_office", "object")
def office() -> list[str]:
    return [
        rect(0, 0, 100, 60, "#E3E9F0"), rect(0, 60, 100, 40, "#C9D2DC"),
        *window(66, 10, 22, 20),
        rect(8, 56, 70, 5, BROWN), rect(12, 61, 4, 28, BROWN_DARK), rect(70, 61, 4, 28, BROWN_DARK),
        rect(22, 30, 32, 22, GREY_DARK, r=2), rect(24.5, 32.5, 27, 17, "#6FA6D9"), rect(34, 52, 8, 4, GREY_DARK),
        rect(56, 50, 14, 6, WHITE, r=1), rect(58, 46, 8, 10, TEAL, r=1.5),
        rect(80, 66, 12, 22, BROWN_LIGHT, r=2), circle(86, 58, 9, GREEN), rect(84, 56, 4, 10, GREEN_DARK),
    ]


@art("place_factory", "place")
def factory() -> list[str]:
    return [
        *sky_and_ground(GREEN_LIGHT, 88),
        circle(22, 14, 6, WHITE, op=0.9), circle(30, 10, 7, WHITE, op=0.9), circle(38, 8, 5, WHITE, op=0.8),
        rect(18, 18, 10, 46, RED_DARK), rect(18, 26, 10, 4, WHITE),
        path("M8 88 V 52 L 28 40 V 52 L 48 40 V 52 L 68 40 V 52 L 88 40 V 88 Z", GREY),
        *[rect(14 + i * 20, 62, 12, 10, YELLOW, op=0.8) for i in range(4)],
        rect(40, 74, 18, 14, GREY_DARK),
    ]


