"""Tests for tools/build_content.py. Run with: python3 -m unittest discover -s tools/tests"""
from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

import build_content as bc  # noqa: E402


def lesson(sentences: list[str], **extras) -> dict:
    return {"structures": ["test"], "sentences": sentences, **extras}


SENTENCES = [
    "Kaixo! Ni Ane naiz. = ¡Hola! Soy Ane. | ¡Hola! Yo soy Ane.",
    "Zu Mikel zara? | Mikel zara? = ¿Tú eres Mikel? | ¿Eres Mikel?",
    "Bai, ni Mikel naiz. = Sí, soy Mikel.",
    "Ez, ni Jon naiz. = No, soy Jon.",
    "Egun on, Itziar! = ¡Buenos días, Itziar!",
    "Gabon eta bihar arte! = ¡Buenas noches y hasta mañana!",
    "Oso ondo, eta zu? = Muy bien, ¿y tú?",
    "Nor zara zu? = ¿Quién eres tú?",
]


class BuildContentTest(unittest.TestCase):
    lemmatizer = bc.Lemmatizer()

    def test_a_sentence_has_basque_and_spanish_variants(self) -> None:
        parsed = bc.parse_sentence("Zu Mikel zara? | Mikel zara? = ¿Tú eres Mikel? | ¿Eres Mikel?")
        self.assertEqual(parsed["eu"], "Zu Mikel zara?")
        self.assertEqual(parsed["eu_alt"], ["Mikel zara?"])
        self.assertEqual(parsed["es"], ["¿Tú eres Mikel?", "¿Eres Mikel?"])

    def test_a_sentence_without_spanish_is_an_error(self) -> None:
        with self.assertRaises(bc.SourceError):
            bc.parse_sentence("Kaixo!")

    def test_sentences_rotate_through_exercise_types(self) -> None:
        built = bc.build_lesson("u01-1.l1.s1", lesson(SENTENCES), offset=0, lemmatizer=self.lemmatizer)
        types = [e["type"] for e in built["exercises"]]
        self.assertGreaterEqual(len(set(types)), 6)
        self.assertEqual([e["id"] for e in built["exercises"]][:2], ["u01-1.l1.s1.e01", "u01-1.l1.s1.e02"])

    def test_word_bank_distractors_are_valid_words_outside_the_answer(self) -> None:
        built = bc.build_lesson("u01-1.l1.s1", lesson(SENTENCES), offset=0, lemmatizer=self.lemmatizer)
        unit = bc.unit_key_from_id("u01-1")
        for exercise in built["exercises"]:
            if exercise["type"] == "translate_wordbank" and exercise["prompt"]["lang"] == "es":
                answer = {w.lower() for w in bc.words(exercise["answers"][0])}
                for word in exercise["distractors"]:
                    self.assertNotIn(word.lower(), answer)
                    self.assertTrue(self.lemmatizer.accepts(word, unit), word)
                    # Parts of multi-word lemmas ("eskerrik" of "eskerrik asko") are not valid alone.
                    self.assertNotIn(word.lower(), {"bihar", "arte", "egun"})

    def test_hand_made_items_are_interleaved_and_tagged(self) -> None:
        extras = {
            "pairs": [["kaixo = hola", "agur = adiós", "bai = sí", "ez = no"]],
            "blanks": [{"text": "Ni Ane ___. = Yo soy Ane.", "options": ["naiz", "zara"]}],
            "chats": [{"messages": ["Ane | Kaixo! Nor zara?"], "options": ["Ni Jon naiz.", "Zu Jon zara."]}],
        }
        built = bc.build_lesson("u01-1.l1.s1", lesson(SENTENCES[:4], **extras), offset=0, lemmatizer=self.lemmatizer)
        types = [e["type"] for e in built["exercises"]]
        self.assertEqual(len(types), 7)
        self.assertEqual(types[2], "match_pairs")
        blank = next(e for e in built["exercises"] if e["type"] == "fill_blank")
        self.assertEqual(blank["correct"], 0)
        self.assertEqual(blank["translation"], "Yo soy Ane.")
        self.assertIn("izan", blank["vocabulary"])
        self.assertTrue(all(e["structures"] == ["test"] for e in built["exercises"]))

    def test_pictures_come_from_the_catalog_with_the_first_one_correct(self) -> None:
        built = bc.build_lesson("u05-1.l1.s1", lesson(SENTENCES[:2], images=["bread, water, milk"]), offset=0,
                                lemmatizer=self.lemmatizer)
        picture = next(e for e in built["exercises"] if e["type"] == "choose_image")
        self.assertEqual(picture["prompt"], "el pan")
        self.assertEqual(picture["options"][picture["correct"]], {"text": "ogia", "image": "food_bread.webp"})
        self.assertEqual(sorted(o["text"] for o in picture["options"]), ["esnea", "ogia", "ura"])
        self.assertEqual(picture["vocabulary"], ["ogi"])

    def test_unknown_or_repeated_pictures_are_errors(self) -> None:
        images = bc.load_images()
        rng = bc.random.Random(0)
        for item in ("bread, unicorn, milk", "bread", "morning, greet_morning, night"):
            with self.subTest(item=item), self.assertRaises(bc.SourceError):
                bc.picture_exercise(item, images, rng)

    def test_every_catalog_picture_has_an_image_and_every_image_is_used(self) -> None:
        assets = bc.ROOT / "core" / "data" / "src" / "main" / "assets" / "images"
        catalog = {row["image"] for row in bc.load_images().values()}
        self.assertEqual(catalog, {path.stem for path in assets.glob("*.webp")})

    def test_the_last_unit_of_a_section_gets_a_section_review(self) -> None:
        source = {
            "id": "u01-2", "section": "s1", "bookUnit": 1, "title": {}, "goals": [], "guidebook": {"sections": []},
            "levels": [{"type": "review"}, {"type": "legendary"}],
        }
        built = bc.build_unit(source, section_last=True)
        self.assertEqual(built["levels"][-1], {
            "id": "u01-2.l3", "type": "review", "title": bc.DEFAULT_TITLES["section_review"], "scope": "section",
        })
        self.assertEqual(len(bc.build_unit(source, section_last=False)["levels"]), 2)

    def test_the_build_is_deterministic_and_current(self) -> None:
        first = bc.build()
        self.assertEqual(first, bc.build())
        for path, text in first.items():
            self.assertEqual(path.read_text(encoding="utf-8"), text, f"{path.name} is out of date")


if __name__ == "__main__":
    unittest.main()
