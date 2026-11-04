"""Tests for tools/validate_content.py. Run with: python3 -m unittest discover -s tools/tests"""
from __future__ import annotations

import json
import shutil
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

import validate_content as vc  # noqa: E402

UNIT = Path("courses/eu-es/units/u01-1.json")


class ValidateContentTest(unittest.TestCase):
    def setUp(self) -> None:
        self.tmp = Path(tempfile.mkdtemp())
        self.assets = self.tmp / "assets"
        self.content = self.tmp / "content"
        shutil.copytree(vc.DEFAULT_ASSETS, self.assets)
        shutil.copytree(vc.DEFAULT_CONTENT, self.content)

    def tearDown(self) -> None:
        shutil.rmtree(self.tmp)

    # Helpers -------------------------------------------------------------------------------

    def unit(self) -> dict:
        return json.loads((self.assets / UNIT).read_text(encoding="utf-8"))

    def save(self, unit: dict) -> None:
        (self.assets / UNIT).write_text(json.dumps(unit, ensure_ascii=False), encoding="utf-8")

    def first_exercise(self, unit: dict, exercise_type: str) -> dict:
        for level in unit["levels"]:
            for lesson in level.get("lessons", []):
                for exercise in lesson["exercises"]:
                    if exercise["type"] == exercise_type:
                        return exercise
        raise AssertionError(f"no {exercise_type} exercise in the sample unit")

    def errors(self) -> list[str]:
        report, _ = vc.validate(self.assets, self.content)
        return report.errors

    def assert_error(self, fragment: str) -> None:
        errors = self.errors()
        self.assertTrue(any(fragment in e for e in errors), f"expected an error containing {fragment!r}, got {errors}")

    # Tests ---------------------------------------------------------------------------------

    def test_bundled_content_is_valid(self) -> None:
        self.assertEqual(self.errors(), [])

    def test_unknown_word_is_reported(self) -> None:
        unit = self.unit()
        self.first_exercise(unit, "speak")["text"] = "Egun on, txakurra!"
        self.save(unit)
        self.assert_error("'txakurra' is not in the syllabus")

    def test_word_from_a_later_unit_is_reported(self) -> None:
        unit = self.unit()
        self.first_exercise(unit, "speak")["text"] = "Egun on, aita!"
        self.save(unit)
        self.assert_error("'aita' is taught later (unit 3.1)")

    def test_inflected_forms_and_proper_nouns_are_accepted(self) -> None:
        unit = self.unit()
        self.first_exercise(unit, "speak")["text"] = "Ni Itziar naiz, eta zu Jon zara."
        self.save(unit)
        self.assertEqual(self.errors(), [])

    def test_declared_lemma_from_a_later_unit_is_reported(self) -> None:
        unit = self.unit()
        self.first_exercise(unit, "speak")["vocabulary"] = ["etxe"]
        self.save(unit)
        self.assert_error("declared lemma 'etxe' belongs to unit 4.1")

    def test_duplicated_id_is_reported(self) -> None:
        unit = self.unit()
        exercises = unit["levels"][0]["lessons"][0]["exercises"]
        exercises[1]["id"] = exercises[0]["id"]
        self.save(unit)
        self.assert_error("duplicated id")

    def test_id_outside_its_parent_is_reported(self) -> None:
        unit = self.unit()
        unit["levels"][0]["lessons"][0]["exercises"][0]["id"] = "u01-1.l9.s1.e01"
        self.save(unit)
        self.assert_error("must start with 'u01-1.l1.s1.'")

    def test_correct_index_out_of_range_is_reported(self) -> None:
        unit = self.unit()
        self.first_exercise(unit, "choose_meaning")["correct"] = 7
        self.save(unit)
        self.assert_error("out of range")

    def test_missing_image_is_reported(self) -> None:
        unit = self.unit()
        self.first_exercise(unit, "choose_image")["options"][0]["image"] = "nope.webp"
        self.save(unit)
        self.assert_error("missing image images/nope.webp")

    def test_schema_error_points_at_the_exercise_field(self) -> None:
        unit = self.unit()
        del self.first_exercise(unit, "fill_blank")["translation"]
        self.save(unit)
        self.assert_error("'translation' is a required property")

    def test_unknown_exercise_type_is_reported(self) -> None:
        unit = self.unit()
        unit["levels"][0]["lessons"][0]["exercises"][0]["type"] = "dance"
        self.save(unit)
        self.assert_error("unknown exercise type 'dance'")

    def test_unlisted_unit_file_is_reported(self) -> None:
        shutil.copy(self.assets / UNIT, self.assets / "courses/eu-es/units/u01-2.json")
        self.assert_error("unit is not listed in course.json")

    def test_wordbank_distractor_in_answer_is_reported(self) -> None:
        unit = self.unit()
        exercise = self.first_exercise(unit, "translate_wordbank")
        exercise["distractors"] = ["soy"]
        self.save(unit)
        self.assert_error("distractors also appear in the answer")

    def test_recorded_audio_leaves_the_pending_list(self) -> None:
        _, before = vc.validate(self.assets, self.content)
        audio = self.assets / "audio"
        audio.mkdir(exist_ok=True)
        (audio / f"{before[0][0]}.ogg").write_bytes(b"")
        _, after = vc.validate(self.assets, self.content)
        self.assertEqual(len(after), len(before) - 1)

    def test_pending_csv_must_be_up_to_date(self) -> None:
        (self.content / "audio" / "pending.csv").write_text("id,text,unit\n", encoding="utf-8")
        code = vc.main(["--assets", str(self.assets), "--content", str(self.content)])
        self.assertEqual(code, 1)


if __name__ == "__main__":
    unittest.main()
