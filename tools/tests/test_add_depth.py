"""Tests for tools/design/add_depth.py. Run with: python3 -m unittest discover -s tools/tests"""
import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "design"))

import add_depth as ad  # noqa: E402


class AddDepthTest(unittest.TestCase):
    def test_translate_moves_absolute_coordinates_only(self) -> None:
        self.assertEqual(ad.translate("M10 10 L20 10 l5 5 H30 V40 Z", 1, 2), "M11 12 L21 12 l5 5 H31 V42 Z")
        self.assertEqual(ad.translate("M0,0 a5,5 0 1,0 10,0", 3, 4), "M3 4 a5 5 0 1 0 10 0")
        self.assertEqual(ad.translate("m5 5 l1 1", 1, 1), "m6 6 l1 1")

    def test_bounds_cover_the_shape(self) -> None:
        self.assertEqual(ad.bounds("M10 20 L50 20 L50 60 Z"), (10, 20, 50, 60))

    def test_every_drawing_has_its_depth(self) -> None:
        for folder in ad.FOLDERS:
            for svg in folder.glob("*.svg"):
                with self.subTest(svg=svg.name):
                    self.assertEqual(svg.read_text(encoding="utf-8"), ad.process(svg),
                                     "run python3 tools/design/add_depth.py")


if __name__ == "__main__":
    unittest.main()
