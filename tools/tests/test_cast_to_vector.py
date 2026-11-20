"""Tests for tools/design/cast_to_vector.py. Run with: python3 -m unittest discover -s tools/tests"""
from __future__ import annotations

import sys
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "design"))

import cast_to_vector as ctv  # noqa: E402

SVG = "http://www.w3.org/2000/svg"


def svg(body: str) -> ET.Element:
    return ET.fromstring(f'<svg xmlns="{SVG}" viewBox="0 0 240 280">{body}</svg>')


class GeneratedFilesTest(unittest.TestCase):
    def test_generated_files_are_up_to_date(self) -> None:
        for path, text in ctv.outputs().items():
            with self.subTest(path=path.name):
                self.assertTrue(path.exists(), "run python3 tools/design/cast_to_vector.py")
                self.assertEqual(path.read_text(encoding="utf-8"), text,
                                 "run python3 tools/design/cast_to_vector.py")

    def test_every_member_has_every_pose(self) -> None:
        for member in ctv.MEMBERS:
            for pose in ctv.POSES:
                self.assertTrue((ctv.ART / f"{member}-{pose}.svg").exists(), f"{member}-{pose}.svg")


class TreeTest(unittest.TestCase):
    def test_still_shapes_merge_and_motions_nest(self) -> None:
        nodes = ctv.tree(svg(
            '<ellipse cx="120" cy="268" rx="50" ry="7"/>'
            '<rect x="0" y="0" width="1" height="1"/>'
            '<g data-motion="Jump" data-pivot="120 264">'
            '  <rect x="0" y="0" width="2" height="2"/>'
            '  <g data-motion="Blink" data-pivot="120 100"><circle cx="104" cy="100" r="4"/></g>'
            '</g>'
        ))
        self.assertEqual(len(nodes), 2)
        self.assertIsInstance(nodes[0], ctv.Layer)
        self.assertEqual(len(nodes[0].elements), 2)
        self.assertIsInstance(nodes[1], ctv.Group)
        self.assertEqual((nodes[1].motion, nodes[1].pivot), ("Jump", (120.0, 264.0)))
        self.assertEqual([n.motion for n in nodes[1].children], [None, "Blink"])

    def test_a_split_group_cannot_carry_paint(self) -> None:
        with self.assertRaises(ValueError):
            ctv.tree(svg('<g fill="#FF0000"><g data-motion="Blink"><circle cx="1" cy="1" r="1"/></g></g>'))

    def test_children_inherit_the_group_fill(self) -> None:
        out = ET.Element("vector")
        ctv.convert(svg('<g fill="#D7263D"><circle cx="1" cy="1" r="1"/></g>')[0], out)
        self.assertEqual(out[0].get(f"{{{ctv.ANDROID}}}fillColor"), "#FFD7263D")


if __name__ == "__main__":
    unittest.main()
