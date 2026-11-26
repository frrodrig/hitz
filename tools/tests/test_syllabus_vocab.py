"""Tests for tools/syllabus_vocab.py. Run with: python3 -m unittest discover -s tools/tests"""
from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

import syllabus_vocab as sv  # noqa: E402


class SyllabusVocabTest(unittest.TestCase):
    def test_each_level_has_its_own_word_range(self) -> None:
        self.assertEqual(sv.core_range("1"), (100, 150))
        self.assertEqual(sv.core_range("4"), (100, 150))
        self.assertEqual(sv.core_range("5"), (150, 200))
        self.assertEqual(sv.core_range("6"), (150, 200))

    def test_the_document_is_in_sync_with_the_csv(self) -> None:
        doc = sv.DOC_PATH.read_text(encoding="utf-8")
        self.assertEqual(sv.render(doc, sv.load()), doc)


if __name__ == "__main__":
    unittest.main()
