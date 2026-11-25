#!/usr/bin/env python3
"""Builds the app's Google Sans Flex from the variable font published in google/fonts.

    pip install fonttools
    python3 tools/design/subset_font.py "GoogleSansFlex[ROND,opsz,slnt,wdth,wght].ttf"

Source: https://github.com/google/fonts/tree/main/ofl/googlesansflex (SIL OFL 1.1, licence in
third_party/fonts/). The app keeps two axes:

- wght 100–900: every weight the type scale uses.
- wdth 100–125: the wide styles of headlines, display text and hero numerals.

Optical size is pinned at its default (18) and roundness and slant at 0: keeping opsz would
add about 400 KB for a difference that is hard to see at phone sizes. The character set is the
Latin subset the app already ships (Spanish, Basque and the punctuation they use), read from the
current font, so regenerating never drops a character.
"""
import io
import sys
from pathlib import Path

from fontTools import subset
from fontTools.ttLib import TTFont
from fontTools.varLib import instancer

ROOT = Path(__file__).resolve().parents[2]
TARGET = ROOT / "core/designsystem/src/main/res/font/core_designsystem_sans_flex.ttf"
AXES = {"opsz": 18, "ROND": 0, "slnt": 0, "wdth": (100, 125), "wght": (100, 900)}


def main(source: str) -> None:
    codepoints = sorted(TTFont(TARGET).getBestCmap())
    font = instancer.instantiateVariableFont(TTFont(source), AXES)
    # The subsetter needs a font read back from bytes after instancing.
    buffer = io.BytesIO()
    font.save(buffer)
    buffer.seek(0)
    font = TTFont(buffer)
    options = subset.Options()
    options.layout_features = ["*"]
    options.name_IDs = ["*"]
    options.notdef_outline = True
    options.hinting = False
    subsetter = subset.Subsetter(options)
    subsetter.populate(unicodes=codepoints)
    subsetter.subset(font)
    font.save(TARGET)
    axes = ", ".join(f"{a.axisTag} {a.minValue:g}–{a.maxValue:g}" for a in font["fvar"].axes)
    print(f"{TARGET.relative_to(ROOT)}: {TARGET.stat().st_size // 1024} KB, {len(codepoints)} characters, {axes}")


if __name__ == "__main__":
    main(sys.argv[1])
