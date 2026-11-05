"""People, family and jobs (units 1.2, 3.1, 3.2, 11.1)."""
from __future__ import annotations

from .kit import (
    BLACK, BLUE, BROWN, BROWN_DARK, GREEN, GREEN_DARK, GREY, GREY_DARK, GREY_LIGHT, HAIR_BLACK, HAIR_BLOND,
    HAIR_BROWN, HAIR_GREY, NAVY, NAVY_LIGHT, ORANGE, ORANGE_DARK, PINK, PURPLE, RED, RED_DARK, SKIN, SKIN_DARK,
    SKY, TEAL, TEAL_DARK, WHITE, YELLOW, art, circle, ellipse, figure, line, path, person, rect, stroke,
)


@art("people_man", "people")
def man() -> list[str]:
    beard = path("M34.5 35 Q 35 55 50 55.5 Q 65 55 65.5 35 Q 61 47.5 50 47.5 Q 39 47.5 34.5 35 Z", HAIR_BROWN)
    return person(shirt=BLUE, shirt_shade="#2D63B3", extras_front=[beard])


@art("people_woman", "people")
def woman() -> list[str]:
    earrings = [circle(34, 39, 1.8, YELLOW), circle(66, 39, 1.8, YELLOW)]
    return person(hair_style="long", hair_color=HAIR_BLACK, shirt=RED, shirt_shade=RED_DARK, extras_front=earrings)


@art("people_boy", "people")
def boy() -> list[str]:
    cap = [path("M36 33 Q 37 18 50 18 Q 63 18 64 33 Z", RED), rect(56, 29, 16, 4, RED_DARK, r=2)]
    return person(top=26, scale=0.85, hair_color=HAIR_BLOND, shirt=GREEN, shirt_shade=GREEN_DARK, extras_front=cap)


@art("people_girl", "people")
def girl() -> list[str]:
    bows = [circle(32, 38, 3, PINK), circle(68, 38, 3, PINK)]
    return person(top=26, scale=0.85, skin=SKIN_DARK, hair_style="pigtails", hair_color=HAIR_BLACK,
                  shirt=PURPLE, shirt_shade="#6E50A3", extras_front=bows)


@art("people_child", "people")
def child() -> list[str]:
    # A toddler with a single curl and a dummy-free smile, smaller than the boy and girl.
    curl = stroke("M50 37 q 4 -6 0 -9 q -4 -1 -3 3", HAIR_BROWN, 2.4)
    return [*person(top=40, scale=0.75, hair_style="bald", hair_color=HAIR_BROWN, shirt=YELLOW,
                    shirt_shade="#D9A21B"), curl]


@art("people_grandfather", "people")
def grandfather() -> list[str]:
    moustache = path("M42 41 Q 50 36.5 58 41 Q 54 44.5 50 41.8 Q 46 44.5 42 41 Z", HAIR_GREY)
    glasses = [circle(44.2, 33.3, 4.2, "none", stroke=NAVY, sw=1.4), circle(55.8, 33.3, 4.2, "none", stroke=NAVY, sw=1.4),
               line(48.4, 33.3, 51.6, 33.3, NAVY, 1.4)]
    return person(hair_style="bald", hair_color=HAIR_GREY, shirt=BROWN, shirt_shade=BROWN_DARK,
                  extras_front=[moustache, *glasses])


@art("people_grandmother", "people")
def grandmother() -> list[str]:
    glasses = [circle(44.2, 33.3, 4.2, "none", stroke=RED_DARK, sw=1.4),
               circle(55.8, 33.3, 4.2, "none", stroke=RED_DARK, sw=1.4), line(48.4, 33.3, 51.6, 33.3, RED_DARK, 1.4)]
    pearls = [circle(42 + i * 4, 70 + (2 if 0 < i < 4 else 0), 1.5, WHITE) for i in range(5)]
    return person(hair_style="bun", hair_color=HAIR_GREY, shirt=TEAL, shirt_shade=TEAL_DARK,
                  extras_front=[*glasses, *pearls])


@art("job_teacher", "people")
def teacher() -> list[str]:
    board = [rect(8, 10, 84, 46, BROWN, r=3), rect(11, 13, 78, 40, GREEN_DARK, r=1.5),
             stroke("M16 24 h 14 M16 31 h 22 M64 22 l 6 6 m 0 -6 l -6 6", WHITE, 1.6, op=0.85)]
    return person(top=30, scale=0.9, hair_style="long", hair_color=HAIR_BROWN, shirt=ORANGE,
                  shirt_shade=ORANGE_DARK, extras_back=board)


@art("job_doctor", "people")
def doctor() -> list[str]:
    coat = [path("M29 100 V 76 Q 29 68 37 68 H 44 L 50 82 L 56 68 H 63 Q 71 68 71 76 V 100 Z", WHITE),
            stroke("M41 69 Q 39 84 46 86 M59 69 Q 61 84 54 86", GREY_DARK, 1.6), circle(50, 87, 2.4, GREY)]
    return person(hair_color=HAIR_BLACK, skin=SKIN_DARK, shirt=SKY, shirt_shade="#9FC4DA", extras_front=coat)


@art("job_nurse", "people")
def nurse() -> list[str]:
    cap = [path("M38 26 L 40 17 H 60 L 62 26 Q 50 23 38 26 Z", WHITE), rect(48.5, 18.5, 3, 7, RED),
           rect(46.5, 20.5, 7, 3, RED)]
    return person(hair_style="bun", hair_color=HAIR_BROWN, shirt=TEAL, shirt_shade=TEAL_DARK, extras_front=cap)


@art("job_cook", "people")
def cook() -> list[str]:
    hat = [circle(42, 16, 7, WHITE), circle(50, 13, 8, WHITE), circle(58, 16, 7, WHITE),
           rect(38, 16, 24, 12, WHITE, r=2), rect(38, 25, 24, 4, GREY_LIGHT)]
    buttons = [circle(50, 80 + i * 7, 1.6, GREY) for i in range(3)]
    return person(top=24, scale=0.95, hair_color=HAIR_BLACK, shirt=WHITE, shirt_shade=GREY_LIGHT,
                  extras_front=[*hat, *buttons])


@art("job_shop_assistant", "people")
def shop_assistant() -> list[str]:
    apron = [path("M37 70 H 63 V 100 H 37 Z", GREEN), rect(44, 78, 12, 7, GREEN_DARK, r=1.5)]
    counter = [rect(0, 88, 100, 12, BROWN), rect(0, 88, 100, 3, BROWN_DARK)]
    return person(hair_style="pigtails", hair_color=HAIR_BLOND, shirt=YELLOW, shirt_shade="#D9A21B",
                  extras_front=[*apron, *counter])


@art("job_driver", "people")
def driver() -> list[str]:
    hat = [path("M34 30 Q 34 17 50 17 Q 66 17 66 30 Z", NAVY), rect(32, 28, 36, 4, BLACK, r=2),
           circle(50, 23, 2, YELLOW)]
    wheel = [circle(50, 88, 17, "none", stroke=BLACK, sw=4), circle(50, 88, 4, BLACK),
             line(50, 88, 36, 82, BLACK, 3), line(50, 88, 64, 82, BLACK, 3)]
    return person(hair_color=HAIR_BROWN, shirt=NAVY_LIGHT, shirt_shade=NAVY, extras_front=[*hat, *wheel])


@art("describe_tall", "people")
def tall() -> list[str]:
    ruler = [rect(80, 8, 8, 82, YELLOW, r=1.5), *[line(80, 14 + i * 9, 84, 14 + i * 9, BROWN_DARK, 1) for i in range(9)]]
    return [*ruler, *figure(48, 92, 84, hair_color=HAIR_BROWN, shirt=TEAL, legs=NAVY)]


@art("describe_short", "people")
def short() -> list[str]:
    ruler = [rect(80, 8, 8, 82, YELLOW, r=1.5), *[line(80, 14 + i * 9, 84, 14 + i * 9, BROWN_DARK, 1) for i in range(9)]]
    return [*ruler, *figure(48, 92, 42, hair_style="long", hair_color=HAIR_BLACK, shirt=ORANGE, skirt=True)]


@art("describe_long_hair", "people")
def long_hair() -> list[str]:
    return person(top=16, hair_style="long", hair_color=HAIR_BROWN, shirt=GREEN, shirt_shade=GREEN_DARK)


@art("describe_short_hair", "people")
def short_hair() -> list[str]:
    return person(top=16, hair_style="short", hair_color=HAIR_BLACK, skin=SKIN_DARK, shirt=PURPLE,
                  shirt_shade="#6E50A3")


@art("describe_happy", "people")
def happy() -> list[str]:
    big_smile = path("M43 39 Q 50 48 57 39 Z", RED_DARK)
    sparkles = [path("M20 22 l 2 5 l 5 2 l -5 2 l -2 5 l -2 -5 l -5 -2 l 5 -2 z", YELLOW),
                path("M80 30 l 1.5 4 l 4 1.5 l -4 1.5 l -1.5 4 l -1.5 -4 l -4 -1.5 l 4 -1.5 z", YELLOW)]
    return [*person(hair_color=HAIR_BLOND, shirt=YELLOW, shirt_shade="#D9A21B", smile=False,
                   extras_front=[big_smile]),
            *sparkles]


@art("describe_tired", "people")
def tired() -> list[str]:
    lids = [rect(40, 29.5, 8, 4, SKIN), rect(52, 29.5, 8, 4, SKIN),
            stroke("M41 33.5 Q 44.2 35.5 47.5 33.5 M52.5 33.5 Q 55.8 35.5 59 33.5", "#1E2A44", 1.4)]
    mouth = ellipse(50, 41.5, 2.6, 2, "#7A2C2C")
    zs = [stroke("M70 18 h 7 l -7 8 h 7 M80 8 h 5 l -5 6 h 5", NAVY_LIGHT, 1.8)]
    return [*person(hair_color=HAIR_BROWN, shirt=GREY, shirt_shade=GREY_DARK, smile=False,
                   extras_front=[*lids, mouth]), *zs]
