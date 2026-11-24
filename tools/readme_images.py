#!/usr/bin/env python3
"""Images for the README, from sources the repository already keeps up to date.

    python3 tools/readme_images.py

* ``docs/images/screenshots/<name>.png``: the Roborazzi reference screenshots listed in
  ``SCREENSHOTS``, scaled to 540 px wide. Re-run it after the references are re-recorded (the
  *UI tests* workflow with ``record_screenshots``) so the README shows the current app.
* ``docs/images/cast.png``: the four cast members in their *Hola* pose, from
  ``tools/design/art/cast/``.

Needs Pillow and ``rsvg-convert`` (librsvg).
"""
from __future__ import annotations

import io
import subprocess
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "docs" / "images"
WIDTH = 540

# README order: first run, the path, two lesson moments, the end, practice, profile, dark theme.
SCREENSHOTS = {
    "onboarding": "feature/onboarding/src/test/screenshots/onboarding_welcome.png",
    "path": "app/src/test/screenshots/app_path.png",
    "lesson": "feature/lesson/src/test/screenshots/lesson_word_bank.png",
    "chat": "feature/lesson/src/test/screenshots/lesson_complete_chat_correct.png",
    "finished": "feature/lesson/src/test/screenshots/lesson_finished.png",
    "practice": "feature/practice/src/test/screenshots/practice_hub.png",
    "profile": "feature/profile/src/test/screenshots/profile.png",
    "path-dark": "feature/path/src/test/screenshots/path_dark.png",
}
CAST = ["ane", "mikel", "maite", "unai"]
CAST_HEIGHT = 280


def screenshots() -> None:
    target = OUT / "screenshots"
    target.mkdir(parents=True, exist_ok=True)
    for stale in target.glob("*.png"):
        if stale.stem not in SCREENSHOTS:
            stale.unlink()
    for name, source in SCREENSHOTS.items():
        image = Image.open(ROOT / source).convert("RGB")
        image = image.resize((WIDTH, round(image.height * WIDTH / image.width)), Image.LANCZOS)
        image.save(target / f"{name}.png", optimize=True)
        print(f"docs/images/screenshots/{name}.png <- {source}")


def cast() -> None:
    members = []
    for member in CAST:
        svg = (ROOT / "tools" / "design" / "art" / "cast" / f"{member}-hola.svg").read_bytes()
        png = subprocess.run(["rsvg-convert", "-h", str(CAST_HEIGHT * 2)], input=svg, capture_output=True, check=True)
        members.append(Image.open(io.BytesIO(png.stdout)).convert("RGBA"))
    gap = 48
    sheet = Image.new("RGBA", (sum(m.width for m in members) + gap * (len(members) - 1), members[0].height), (0, 0, 0, 0))
    x = 0
    for image in members:
        sheet.alpha_composite(image, (x, 0))
        x += image.width + gap
    sheet.save(OUT / "cast.png", optimize=True)
    print("docs/images/cast.png")


if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    screenshots()
    cast()
