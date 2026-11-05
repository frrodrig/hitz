#!/usr/bin/env python3
"""Renders the course illustrations into the app's assets.

The drawings live in ``tools/design/illustrations/`` as Python that writes SVG (see ``kit.py``).
Each one becomes ``core/data/src/main/assets/images/<id>.webp`` at 384 × 384, lossless.

  python3 tools/design/make_illustrations.py                 # render everything
  python3 tools/design/make_illustrations.py --only food_bread,drink_water
  python3 tools/design/make_illustrations.py --sheet out.png # also a contact sheet for review

Needs ``rsvg-convert`` (librsvg) and Pillow.
"""
from __future__ import annotations

import argparse
import io
import subprocess
import sys
from pathlib import Path

from PIL import Image, ImageDraw

sys.path.insert(0, str(Path(__file__).resolve().parent))
from illustrations import load  # noqa: E402
from illustrations.kit import svg  # noqa: E402

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "core" / "data" / "src" / "main" / "assets" / "images"
SIZE = 384


def render(image_id: str) -> Image.Image:
    background, draw = load()[image_id]
    source = svg(background, draw()).encode()
    png = subprocess.run(["rsvg-convert", "-w", str(SIZE), "-h", str(SIZE)], input=source,
                         capture_output=True, check=True).stdout
    return Image.open(io.BytesIO(png)).convert("RGBA")


def contact_sheet(images: dict[str, Image.Image], columns: int = 8, cell: int = 160) -> Image.Image:
    rows = (len(images) + columns - 1) // columns
    sheet = Image.new("RGB", (columns * cell, rows * (cell + 16)), "white")
    pen = ImageDraw.Draw(sheet)
    for index, (image_id, image) in enumerate(images.items()):
        x, y = (index % columns) * cell, (index // columns) * (cell + 16)
        thumb = image.resize((cell - 8, cell - 8))
        sheet.paste(thumb, (x + 4, y + 4), thumb)
        pen.text((x + 4, y + cell - 2), image_id[:24], fill="black")
    return sheet


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--only", help="comma-separated image ids")
    parser.add_argument("--sheet", type=Path, help="write a contact sheet PNG here")
    parser.add_argument("--no-write", action="store_true", help="do not touch the assets")
    args = parser.parse_args(argv)

    ids = sorted(load())
    if args.only:
        wanted = set(args.only.split(","))
        unknown = wanted - set(ids)
        if unknown:
            print(f"unknown illustrations: {sorted(unknown)}", file=sys.stderr)
            return 1
        ids = [i for i in ids if i in wanted]
    rendered = {image_id: render(image_id) for image_id in ids}
    if not args.no_write:
        ASSETS.mkdir(parents=True, exist_ok=True)
        for image_id, image in rendered.items():
            image.save(ASSETS / f"{image_id}.webp", "WEBP", lossless=True, method=6)
    if args.sheet:
        contact_sheet(rendered).save(args.sheet)
    print(f"{len(rendered)} illustrations rendered")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
