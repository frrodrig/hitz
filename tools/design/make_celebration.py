#!/usr/bin/env python3
"""Generates the celebration animation (Lottie JSON, original) shown when a session ends.

Confetti in the brand colours bursts out from the centre, turns, falls a little and fades, with an
expanding ring. Deterministic: run it again and the file is the same.
Run: python3 tools/design/make_celebration.py
"""
from __future__ import annotations

import json
import math
import random
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "core" / "designsystem" / "src" / "main" / "res" / "raw" / "core_designsystem_celebration.json"
SIZE = 240
FRAMES = 72  # 1.2 s at 60 fps
CENTER = SIZE / 2
# Brand red, teal, amber and a light blush.
COLOURS = ["#C8102E", "#4A6366", "#E8A33D", "#FFB3AE", "#7DB6BB"]
PIECES = 22


def rgba(hex_colour: str) -> list[float]:
    value = hex_colour.lstrip("#")
    return [round(int(value[i:i + 2], 16) / 255, 4) for i in (0, 2, 4)] + [1]


def static(value) -> dict:
    return {"a": 0, "k": value}


def keyframes(*frames: tuple[int, list[float]]) -> dict:
    ease_out = {"i": {"x": [0.2], "y": [1]}, "o": {"x": [0.3], "y": [0]}}
    out = [{"t": t, "s": value, **ease_out} for t, value in frames[:-1]]
    out.append({"t": frames[-1][0], "s": frames[-1][1]})
    return {"a": 1, "k": out}


def transform(position: dict, rotation: dict, opacity: dict, scale: dict | None = None) -> dict:
    return {
        "o": opacity,
        "r": rotation,
        "p": position,
        "a": static([0, 0, 0]),
        "s": scale or static([100, 100, 100]),
    }


def group(items: list[dict]) -> dict:
    return {"ty": "gr", "it": items + [{
        "ty": "tr", "p": static([0, 0]), "a": static([0, 0]), "s": static([100, 100]),
        "r": static(0), "o": static(100),
    }]}


def confetti(index: int, rng: random.Random) -> dict:
    angle = 2 * math.pi * index / PIECES + rng.uniform(-0.15, 0.15)
    distance = rng.uniform(70, 110)
    burst = [CENTER + distance * math.cos(angle), CENTER + distance * math.sin(angle)]
    fallen = [burst[0] + rng.uniform(-8, 8), burst[1] + rng.uniform(18, 34)]
    width, height = rng.choice([(10, 5), (8, 8), (12, 4)])
    shape = {"ty": "rc", "d": 1, "s": static([width, height]), "p": static([0, 0]), "r": static(2)}
    fill = {"ty": "fl", "c": static(rgba(COLOURS[index % len(COLOURS)])), "o": static(100), "r": 1}
    spin = rng.choice([-1, 1]) * rng.uniform(180, 540)
    return {
        "ddd": 0, "ind": index + 2, "ty": 4, "nm": f"confetti {index}", "sr": 1, "ao": 0, "bm": 0,
        "ks": transform(
            position=keyframes((0, [CENTER, CENTER, 0]), (30, burst + [0]), (FRAMES, fallen + [0])),
            rotation=keyframes((0, [0]), (FRAMES, [round(spin)])),
            opacity=keyframes((0, [100]), (44, [100]), (FRAMES, [0])),
        ),
        "shapes": [group([shape, fill])],
        "ip": 0, "op": FRAMES, "st": 0,
    }


def ring() -> dict:
    ellipse = {"ty": "el", "d": 1, "s": static([60, 60]), "p": static([0, 0])}
    stroke = {"ty": "st", "c": static(rgba(COLOURS[2])), "o": static(100), "w": static(6), "lc": 2, "lj": 2}
    return {
        "ddd": 0, "ind": 1, "ty": 4, "nm": "ring", "sr": 1, "ao": 0, "bm": 0,
        "ks": transform(
            position=static([CENTER, CENTER, 0]),
            rotation=static(0),
            opacity=keyframes((0, [90]), (36, [0])),
            scale=keyframes((0, [40, 40, 100]), (36, [320, 320, 100])),
        ),
        "shapes": [group([ellipse, stroke])],
        "ip": 0, "op": 36, "st": 0,
    }


def main() -> None:
    rng = random.Random(7)
    animation = {
        "v": "5.7.4", "fr": 60, "ip": 0, "op": FRAMES, "w": SIZE, "h": SIZE, "nm": "celebration", "ddd": 0,
        "assets": [],
        "layers": [ring()] + [confetti(i, rng) for i in range(PIECES)],
    }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(animation, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"{OUT.relative_to(ROOT)}: {OUT.stat().st_size} bytes")


if __name__ == "__main__":
    main()
