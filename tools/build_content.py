#!/usr/bin/env python3
"""Builds the course JSON from the authoring sources in content/source/.

Authors write each unit as YAML (``content/source/units/<unit>.yaml``): its guidebook, and for each
lesson a bank of sentences plus a few hand-made items (pairs, gaps, meanings, chats, true/false).
This script turns every sentence into one exercise, rotating the exercise types so each lesson
mixes translation, listening, ordering and speaking, and picks word-bank distractors from the
lesson's other sentences. Ids are positional (``u01-1.l1.s1.e01``), so they are stable as long as
the source keeps its order.

  python3 tools/build_content.py           # write the JSON into the app's assets
  python3 tools/build_content.py --check   # fail if the JSON is out of date (CI)
  python3 tools/build_content.py --only u05-1,u05-2

See docs/content-guide.md for the source format.
"""
from __future__ import annotations

import argparse
import csv
import json
import random
import re
import sys
from pathlib import Path

import yaml

sys.path.insert(0, str(Path(__file__).resolve().parent))
from validate_content import (  # noqa: E402
    MAX_SUFFIX, MIN_STEM, basque_texts, clean_lemma, name_words, tokens, unit_key, unit_key_from_id,
)

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "content" / "source"
ASSETS = ROOT / "core" / "data" / "src" / "main" / "assets" / "courses" / "eu-es"
SCHEMA_VERSION = 1

# Exercise types made from a sentence, in rotation. Each lesson starts at a different point.
ROTATION = [
    "translate_wordbank_eu",  # Basque prompt, Spanish tiles
    "translate_wordbank_es",  # Spanish prompt, Basque tiles
    "listen_tap",
    "translate_typing_es",    # Spanish prompt, type Basque
    "order_sentence",
    "translate_typing_eu",    # Basque prompt, type Spanish
    "listen_type",
    "speak",
]
MIN_DISTRACTORS = 2
MAX_DISTRACTORS = 4
EDGE_PUNCTUATION = re.compile(r"^[¡¿!?.,;:«»\"“”()…—–-]+|[¡¿!?.,;:«»\"“”()…—–-]+$")

DEFAULT_TITLES = {
    "review": {"es": "Repaso de la unidad", "eu": "Unitatearen errepasoa"},
    "legendary": {"es": "Nivel legendario", "eu": "Maila legendarioa"},
    "section_review": {"es": "Repaso de la sección", "eu": "Atalaren errepasoa"},
}


class SourceError(Exception):
    pass


class Lemmatizer:
    """Finds the syllabus lemmas a Basque text uses, with the validator's rules."""

    def __init__(self, content: Path = ROOT / "content") -> None:
        self.lemmas: dict[str, tuple[int, int]] = {}
        with (content / "syllabus" / "vocabulary.csv").open(encoding="utf-8") as fh:
            for row in csv.DictReader(fh):
                lemma = clean_lemma(row["lemma"])
                key = unit_key(row["app_unit"])
                self.lemmas[lemma] = min(key, self.lemmas.get(lemma, key))
        self.forms: dict[str, str] = {}
        self.form_units: dict[str, tuple[int, int]] = {}
        for forms_file in sorted((content / "syllabus" / "forms").glob("u*.csv")):
            key = unit_key_from_id(forms_file.stem)
            with forms_file.open(encoding="utf-8") as fh:
                for row in csv.DictReader(fh):
                    form = row["form"].strip().lower()
                    self.forms.setdefault(form, clean_lemma(row["lemma"]))
                    self.form_units[form] = min(key, self.form_units.get(form, key))
        with (content / "syllabus" / "proper-nouns.csv").open(encoding="utf-8") as fh:
            self.proper_nouns = {word for row in csv.DictReader(fh) for word in name_words(row["name"])}

    def accepts(self, word: str, unit: tuple[int, int]) -> bool:
        """Whether the validator accepts [word] on its own (multi-word lemmas do not count)."""
        token = word.lower()
        known = {lemma for lemma, key in self.lemmas.items() if key <= unit and " " not in lemma}
        if token in known or token in self.proper_nouns or token.isdigit():
            return True
        if token in self.forms and self.form_units[token] <= unit:
            return True
        stems = [s for s in known if len(s) >= MIN_STEM] + [n for n in self.proper_nouns if len(n) >= MIN_STEM]
        return any(token.startswith(s) and len(token) - len(s) <= MAX_SUFFIX for s in stems)

    def lemmas_in(self, texts: list[str], unit: tuple[int, int]) -> list[str]:
        known = {lemma for lemma, key in self.lemmas.items() if key <= unit}
        # Longest first, then alphabetical: the output must not depend on set order.
        phrases = sorted((lemma for lemma in known if " " in lemma), key=lambda x: (-len(x), x))
        stems = sorted((lemma for lemma in known if " " not in lemma and len(lemma) >= MIN_STEM), key=lambda x: (-len(x), x))
        found: list[str] = []
        for text in texts:
            normalized = " " + " ".join(tokens(text)) + " "
            for phrase in phrases:
                if f" {phrase} " in normalized:
                    found.append(phrase)
                    normalized = normalized.replace(f" {phrase} ", " ")
            for token in normalized.split():
                if token in known:
                    found.append(token)
                elif token in self.forms and self.forms[token] in known:
                    found.append(self.forms[token])
                else:
                    stem = next((s for s in stems if token.startswith(s) and len(token) - len(s) <= MAX_SUFFIX), None)
                    if stem:
                        found.append(stem)
        return list(dict.fromkeys(found))


def words(sentence: str) -> list[str]:
    """Tiles of a sentence, as the app makes them (WordBank.words)."""
    return [w for w in (EDGE_PUNCTUATION.sub("", part) for part in sentence.split(" ")) if w]


def as_list(value: str | list[str]) -> list[str]:
    return [value] if isinstance(value, str) else list(value)


def split_alternatives(text: str) -> list[str]:
    return [part.strip() for part in text.split(" | ") if part.strip()]


def parse_sentence(item: str | dict) -> dict:
    """'Basque | Basque variant = Spanish | Spanish variant' or a dict with eu/es/eu_alt."""
    if isinstance(item, dict):
        if "text" in item:
            parsed = parse_sentence(item["text"])
            return {**{k: v for k, v in item.items() if k != "text"}, **parsed}
        return item
    if " = " not in item:
        raise SourceError(f"sentence {item!r} needs 'Basque = Spanish'")
    eu, es = item.split(" = ", 1)
    eu_answers = split_alternatives(eu)
    return {"eu": eu_answers[0], "eu_alt": eu_answers[1:], "es": split_alternatives(es)}


def parse_pair(item: str | list[str]) -> tuple[str, str]:
    if isinstance(item, str):
        if " = " not in item:
            raise SourceError(f"pair {item!r} needs 'Basque = Spanish'")
        eu, es = item.split(" = ", 1)
        return eu.strip(), es.strip()
    return item[0], item[1]


def parse_message(item: str | list[str]) -> tuple[str, str]:
    """'Speaker | text'."""
    if isinstance(item, str):
        if " | " not in item:
            raise SourceError(f"chat message {item!r} needs 'Speaker | text'")
        speaker, text = item.split(" | ", 1)
        return speaker.strip(), text.strip()
    return item[0], item[1]


def parse_story(story: dict) -> dict:
    """Lines as 'Speaker | Basque = Spanish'; questions as dicts."""
    lines = []
    for line in story["lines"]:
        if isinstance(line, str):
            speaker, text = parse_message(line)
            if " = " not in text:
                raise SourceError(f"story line {line!r} needs 'Speaker | Basque = Spanish'")
            eu, es = text.split(" = ", 1)
            line = {"speaker": speaker, "eu": eu.strip(), "es": es.strip()}
        lines.append(line)
    questions = [{"afterLine": q["afterLine"], "prompt": q["prompt"], "options": q["options"],
                  "correct": q.get("correct", 0)} for q in story.get("questions", [])]
    return {"title": story["title"], "lines": lines, "questions": questions}


def tagged(exercise: dict, item: dict) -> dict:
    for key in ("vocabulary", "structures"):
        if item.get(key):
            exercise[key] = list(item[key])
    return exercise


def tagging_texts(exercise: dict) -> list[str]:
    """Basque text an exercise practises: as the validator's, minus distractors and wrong options."""
    kept = {k: v for k, v in exercise.items() if k != "distractors"}
    if exercise["type"] == "fill_blank":
        return [exercise["sentence"].replace("___", exercise["options"][exercise["correct"]])]
    if exercise["type"] == "complete_chat":
        return [m["text"] for m in exercise["messages"]] + [exercise["options"][exercise["correct"]]]
    if exercise["type"] == "choose_image":
        return [exercise["options"][exercise["correct"]]["text"]]
    return basque_texts(kept)


def distractors(answer: str, pool: list[str], rng: random.Random) -> list[str]:
    """Words from other sentences that are not in the answer (case-insensitive)."""
    taken = {w.lower() for w in words(answer)}
    candidates = sorted({w for w in pool if w.lower() not in taken and not w.isdigit()}, key=lambda w: (w.lower(), w))
    # Keep one spelling per word.
    unique: dict[str, str] = {}
    for word in candidates:
        unique.setdefault(word.lower(), word)
    choices = list(unique.values())
    rng.shuffle(choices)
    count = min(len(choices), max(MIN_DISTRACTORS, min(MAX_DISTRACTORS, len(taken) // 2 + 1)))
    return choices[:count]


def sentence_exercise(kind: str, item: dict, eu_pool: list[str], es_pool: list[str], rng: random.Random) -> dict:
    """One exercise from a sentence. [eu_pool] only holds words that are valid on their own."""
    eu = item["eu"]
    eu_answers = [eu] + as_list(item.get("eu_alt", []))
    es_answers = as_list(item["es"])
    if kind == "translate_wordbank_eu":
        exercise = {"type": "translate_wordbank", "prompt": {"lang": "eu", "text": eu}, "answers": es_answers,
                    "distractors": distractors(es_answers[0], es_pool, rng)}
    elif kind == "translate_wordbank_es":
        exercise = {"type": "translate_wordbank", "prompt": {"lang": "es", "text": es_answers[0]},
                    "answers": eu_answers, "distractors": distractors(eu, eu_pool, rng)}
    elif kind == "translate_typing_es":
        exercise = {"type": "translate_typing", "prompt": {"lang": "es", "text": es_answers[0]}, "answers": eu_answers}
    elif kind == "translate_typing_eu":
        exercise = {"type": "translate_typing", "prompt": {"lang": "eu", "text": eu}, "answers": es_answers}
    elif kind == "listen_tap":
        exercise = {"type": "listen_tap", "text": eu, "distractors": distractors(eu, eu_pool, rng)}
    elif kind == "listen_type":
        exercise = {"type": "listen_type", "text": eu, "answers": [eu]}
    elif kind == "order_sentence":
        exercise = {"type": "order_sentence", "answer": eu, "translation": es_answers[0]}
    elif kind == "speak":
        exercise = {"type": "speak", "text": eu, "translation": es_answers[0]}
    else:
        raise SourceError(f"unknown exercise kind {kind}")
    return tagged(exercise, item)


def allowed_kinds(item: dict) -> list[str]:
    """Order needs three words or more; 'skip' lets authors leave out kinds for a sentence."""
    skip = set(item.get("skip", []))
    kinds = [k for k in ROTATION if k not in skip and k.removesuffix("_eu").removesuffix("_es") not in skip]
    if len(words(item["eu"])) < 3:
        kinds = [k for k in kinds if k != "order_sentence"]
    return kinds


def extra_exercises(lesson: dict) -> list[dict]:
    """Hand-made items, in a fixed order of kinds."""
    out: list[dict] = []
    for pairs in lesson.get("pairs", []):
        items = pairs["items"] if isinstance(pairs, dict) else pairs
        exercise = {"type": "match_pairs", "pairs": [{"eu": eu, "es": es} for eu, es in map(parse_pair, items)]}
        out.append(tagged(exercise, pairs if isinstance(pairs, dict) else {}))
    for blank in lesson.get("blanks", []):
        if "text" in blank:
            sentence, translation = blank["text"].split(" = ", 1)
            blank = {**blank, "sentence": sentence.strip(), "translation": translation.strip()}
        out.append(tagged({"type": "fill_blank", "sentence": blank["sentence"], "options": blank["options"],
                           "correct": blank.get("correct", 0), "translation": blank["translation"]}, blank))
    for meaning in lesson.get("meanings", []):
        out.append(tagged({"type": "choose_meaning", "text": meaning["text"], "options": meaning["options"],
                           "correct": meaning.get("correct", 0)}, meaning))
    for chat in lesson.get("chats", []):
        messages = [{"speaker": speaker, "text": text} for speaker, text in map(parse_message, chat["messages"])]
        out.append(tagged({"type": "complete_chat", "messages": messages, "options": chat["options"],
                           "correct": chat.get("correct", 0)}, chat))
    for item in lesson.get("true_false", []):
        out.append(tagged({"type": "read_true_false", "passage": item["passage"], "statement": item["statement"],
                           "answer": item["answer"]}, item))
    for image in lesson.get("images", []):
        out.append(tagged({"type": "choose_image", "prompt": image["prompt"], "options": image["options"],
                           "correct": image.get("correct", 0)}, image))
    return out


def build_lesson(lesson_id: str, lesson: dict, offset: int, lemmatizer: Lemmatizer | None = None) -> dict:
    sentences = [parse_sentence(item) for item in lesson.get("sentences", [])]
    unit = unit_key_from_id(lesson_id.split(".")[0])
    eu_pool = [w for s in sentences for w in words(s["eu"]) if lemmatizer is None or lemmatizer.accepts(w, unit)]
    es_pool = [w for s in sentences for w in words(as_list(s["es"])[0])]
    made: list[dict] = []
    for index, item in enumerate(sentences):
        kinds = allowed_kinds(item)
        if not kinds:
            raise SourceError(f"{lesson_id}: sentence {item['eu']!r} allows no exercise kind")
        wanted = ROTATION[(index + offset) % len(ROTATION)]
        kind = wanted if wanted in kinds else kinds[(index + offset) % len(kinds)]
        if "kind" in item:
            kind = item["kind"]
        rng = random.Random(f"{lesson_id}.{index}")
        made.append(sentence_exercise(kind, item, eu_pool, es_pool, rng))
    extras = extra_exercises(lesson)
    # Interleave: a hand-made item after every sentence exercise while there are any left.
    merged: list[dict] = []
    for index, exercise in enumerate(made):
        merged.append(exercise)
        if index >= 1 and extras:
            merged.append(extras.pop(0))
    merged += extras
    for number, exercise in enumerate(merged, start=1):
        exercise["id"] = f"{lesson_id}.e{number:02d}"
        if "structures" not in exercise and lesson.get("structures"):
            exercise["structures"] = list(lesson["structures"])
        if "vocabulary" not in exercise and lemmatizer is not None:
            found = lemmatizer.lemmas_in(tagging_texts(exercise), unit_key_from_id(lesson_id.split(".")[0]))
            if found:
                exercise["vocabulary"] = found
    ordered = []
    for exercise in merged:
        head = {"id": exercise.pop("id"), "type": exercise.pop("type")}
        tail = {k: exercise.pop(k) for k in ("vocabulary", "structures") if k in exercise}
        ordered.append({**head, **exercise, **tail})
    return {"id": lesson_id, "exercises": ordered}


def build_unit(source: dict, section_last: bool, lemmatizer: Lemmatizer | None = None) -> dict:
    unit_id = source["id"]
    levels = []
    lesson_levels = 0
    for number, level in enumerate(source["levels"], start=1):
        level_id = f"{unit_id}.l{number}"
        kind = level["type"]
        title = level.get("title") or DEFAULT_TITLES.get(kind)
        if title is None:
            raise SourceError(f"{level_id}: a {kind} level needs a title")
        if kind == "lesson":
            lessons = [
                build_lesson(f"{level_id}.s{n}", lesson, offset=lesson_levels * 3 + n, lemmatizer=lemmatizer)
                for n, lesson in enumerate(level["lessons"], start=1)
            ]
            lesson_levels += 1
            levels.append({"id": level_id, "type": "lesson", "title": title, "lessons": lessons})
        elif kind == "story":
            story = {"id": f"{level_id}.story", **parse_story(level["story"])}
            levels.append({"id": level_id, "type": "story", "title": title, "story": story})
        elif kind in ("review", "legendary"):
            levels.append({"id": level_id, "type": kind, "title": title})
        else:
            raise SourceError(f"{level_id}: unknown level type {kind}")
    if section_last:
        levels.append({
            "id": f"{unit_id}.l{len(levels) + 1}",
            "type": "review",
            "title": DEFAULT_TITLES["section_review"],
            "scope": "section",
        })
    return {
        "schemaVersion": SCHEMA_VERSION,
        "id": unit_id,
        "section": source["section"],
        "bookUnit": source["bookUnit"],
        "title": source["title"],
        "goals": source["goals"],
        "guidebook": source["guidebook"],
        "levels": levels,
    }


def dump(data: dict) -> str:
    return json.dumps(data, ensure_ascii=False, indent=2) + "\n"


def build(only: set[str] | None = None) -> dict[Path, str]:
    """Returns the files to write: path → content."""
    course = yaml.safe_load((SOURCE / "course.yaml").read_text(encoding="utf-8"))
    outputs: dict[Path, str] = {}
    lemmatizer = Lemmatizer()
    last_units = {section["units"][-1] for section in course["sections"] if section["units"]}
    listed = [unit for section in course["sections"] for unit in section["units"]]
    for unit_id in listed:
        if only is not None and unit_id not in only:
            continue
        path = SOURCE / "units" / f"{unit_id}.yaml"
        if not path.exists():
            continue  # not written yet: the validator reports listed units without a file
        source = yaml.safe_load(path.read_text(encoding="utf-8"))
        if source["id"] != unit_id:
            raise SourceError(f"{path.name}: id {source['id']!r} does not match the file name")
        outputs[ASSETS / "units" / f"{unit_id}.json"] = dump(build_unit(source, unit_id in last_units, lemmatizer))
    if only is None:
        written = [u for u in listed if (SOURCE / "units" / f"{u}.yaml").exists()]
        sections = []
        for section in course["sections"]:
            units = [u for u in section["units"] if u in written]
            if units:
                sections.append({**{k: v for k, v in section.items() if k != "units"}, "units": units})
        course_json = {k: v for k, v in course.items() if k != "sections"}
        outputs[ASSETS / "course.json"] = dump({"schemaVersion": SCHEMA_VERSION, **course_json, "sections": sections})
    return outputs


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.split("\n\n")[0])
    parser.add_argument("--check", action="store_true", help="fail if the generated JSON is out of date")
    parser.add_argument("--only", help="comma-separated unit ids to build")
    args = parser.parse_args(argv)
    try:
        outputs = build(set(args.only.split(",")) if args.only else None)
    except (SourceError, KeyError, TypeError) as error:
        print(f"ERROR   {error!r}")
        return 1
    stale = [path for path, text in outputs.items() if not path.exists() or path.read_text(encoding="utf-8") != text]
    if args.check:
        for path in stale:
            print(f"ERROR   {path.relative_to(ROOT)} is out of date; run tools/build_content.py")
        if not args.only:
            known = {p.name for p in outputs if p.parent.name == "units"}
            for orphan in sorted((ASSETS / "units").glob("*.json")):
                if orphan.name not in known:
                    print(f"ERROR   {orphan.relative_to(ROOT)} has no source in content/source/units/")
                    stale.append(orphan)
        return 1 if stale else 0
    for path in stale:
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(outputs[path], encoding="utf-8")
    print(f"{len(outputs)} file(s) built, {len(stale)} updated")
    return 0


if __name__ == "__main__":
    sys.exit(main())
