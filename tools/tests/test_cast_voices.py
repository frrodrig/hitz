"""Tests for tools/audio/cast_voices.py: who says each clip (no synthesis, no Piper needed)."""
from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "audio"))
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

import cast_voices as cv  # noqa: E402
import validate_content as vc  # noqa: E402


class CastVoicesTest(unittest.TestCase):
    speakers = cv.Speakers(vc.DEFAULT_CONTENT)

    def test_the_cast_speaks_with_its_natural_voice(self) -> None:
        self.assertEqual(cv.CAST, {"ane": "maider", "maite": "maider", "mikel": "antton", "unai": "antton"})
        for member, voice in cv.CAST.items():
            self.assertEqual(self.speakers.voices[member.capitalize()], voice)

    def test_a_role_takes_the_voice_of_the_other_gender(self) -> None:
        cast = self.speakers.dialogue(["Saltzailea", "Mikel", "Saltzailea"])
        self.assertEqual(cast, {"Saltzailea": "maider", "Mikel": "antton"})
        self.assertEqual(self.speakers.dialogue(["Ane", "Medikua"])["Medikua"], "antton")

    def test_the_answer_comes_from_the_person_addressed(self) -> None:
        last = {"speaker": "Ane", "text": "Egun on, Iker!"}
        self.assertEqual(cv.answer_voice(last, self.speakers, {"Ane": "maider"}), "antton")

    def test_without_a_name_the_answer_takes_the_other_voice(self) -> None:
        last = {"speaker": "Mikel", "text": "Zer moduz?"}
        self.assertEqual(cv.answer_voice(last, self.speakers, {"Mikel": "antton"}), "maider")

    def test_the_same_text_and_voice_share_one_file(self) -> None:
        self.assertEqual(cv.clip_file("maider", "kaixo"), cv.clip_file("maider", " kaixo "))
        self.assertNotEqual(cv.clip_file("maider", "kaixo"), cv.clip_file("antton", "kaixo"))

    def test_every_course_clip_has_a_voice(self) -> None:
        clips = cv.wanted(vc.DEFAULT_ASSETS, vc.DEFAULT_CONTENT)
        self.assertTrue(clips)
        self.assertEqual({voice for voice, _ in clips.values()}, {"maider", "antton"})


if __name__ == "__main__":
    unittest.main()
