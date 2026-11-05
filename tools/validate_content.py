#!/usr/bin/env python3
"""Validates the Hitz course content.

Checks, for every course under ``<assets>/courses/``:

* JSON Schema (``content/schema/*.schema.json``);
* ids: unique across the course and nested (``unit.level.lesson.exercise``);
* internal consistency: answer indexes, blanks, word banks, story questions, duplicated answers;
* references: every image exists in ``<assets>/images/``; audio is matched against
  ``<assets>/audio/`` and missing clips are listed in ``content/audio/pending.csv``;
* vocabulary: every Basque word is a lemma of the syllabus taught in this unit or earlier, a known
  inflected form (``content/syllabus/forms/<unit>.csv``), a proper noun or a number. Declared
  ``vocabulary`` lemmas must exist and must not belong to a later unit.

Usage::

    python3 tools/validate_content.py            # validate and check pending.csv is up to date
    python3 tools/validate_content.py --write    # validate and regenerate pending.csv

Requires ``jsonschema`` (``pip install jsonschema``). Exits with status 1 when there are errors.
"""
from __future__ import annotations

import argparse
import csv
import io
import json
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DEFAULT_ASSETS = ROOT / "core" / "data" / "src" / "main" / "assets"
DEFAULT_CONTENT = ROOT / "content"

AUDIO_EXTENSION = ".ogg"
MIN_STEM = 3          # shortest lemma that may take suffixes (etxe → etxea, etxean…)
MAX_SUFFIX = 7        # longest suffix accepted after a lemma stem
PUNCTUATION = re.compile(r"[¡!¿?.,;:«»\"“”()\[\]…—–]")
UNIT_ID = re.compile(r"^u(\d{2})-(\d)$")


# --------------------------------------------------------------------------------------------
# Syllabus
# --------------------------------------------------------------------------------------------

def unit_key(app_unit: str) -> tuple[int, int]:
    """'3.2' → (3, 2)."""
    major, minor = app_unit.split(".")
    return int(major), int(minor)


def unit_key_from_id(unit_id: str) -> tuple[int, int]:
    """'u03-2' → (3, 2)."""
    match = UNIT_ID.match(unit_id)
    if not match:
        raise ValueError(f"invalid unit id {unit_id!r}")
    return int(match.group(1)), int(match.group(2))


def name_words(name: str) -> list[str]:
    """Words of a proper noun. A final -a may drop when the name is declined (Euskal Herria →
    Euskal Herrian, Iruñea → Iruñean, Bizkaia → Bizkaiko), so the stem without it counts too."""
    parts = name.lower().split()
    if parts[-1].endswith("a") and len(parts[-1]) >= 5:
        parts.append(parts[-1][:-1])
    return parts


def clean_lemma(lemma: str) -> str:
    """Drops qualifiers such as 'hori (kolorea)' → 'hori'."""
    return re.sub(r"\s*\(.*?\)", "", lemma).strip().lower()


@dataclass
class Syllabus:
    lemmas: dict[str, tuple[int, int]] = field(default_factory=dict)
    forms: dict[str, tuple[int, int]] = field(default_factory=dict)
    proper_nouns: set[str] = field(default_factory=set)

    @classmethod
    def load(cls, content: Path) -> Syllabus:
        syllabus = cls()
        with (content / "syllabus" / "vocabulary.csv").open(encoding="utf-8") as fh:
            for row in csv.DictReader(fh):
                key = unit_key(row["app_unit"])
                lemma = clean_lemma(row["lemma"])
                # A homograph keeps the earliest unit in which it is taught.
                syllabus.lemmas[lemma] = min(key, syllabus.lemmas.get(lemma, key))
        # One file per app unit (forms/u02-1.csv): the forms whose grammar that unit teaches.
        for forms_file in sorted((content / "syllabus" / "forms").glob("u*.csv")):
            key = unit_key_from_id(forms_file.stem)
            with forms_file.open(encoding="utf-8") as fh:
                for row in csv.DictReader(fh):
                    form = row["form"].strip().lower()
                    syllabus.forms[form] = min(key, syllabus.forms.get(form, key))
        nouns_file = content / "syllabus" / "proper-nouns.csv"
        if nouns_file.exists():
            with nouns_file.open(encoding="utf-8") as fh:
                # Each word of a multi-word name (Euskal Herria) counts on its own.
                syllabus.proper_nouns = {word for row in csv.DictReader(fh) for word in name_words(row["name"])}
        return syllabus

    def allowed(self, entries: dict[str, tuple[int, int]], unit: tuple[int, int]) -> set[str]:
        return {word for word, key in entries.items() if key <= unit}


# --------------------------------------------------------------------------------------------
# Reporting
# --------------------------------------------------------------------------------------------

@dataclass
class Report:
    errors: list[str] = field(default_factory=list)
    warnings: list[str] = field(default_factory=list)

    def error(self, where: str, message: str) -> None:
        self.errors.append(f"{where}: {message}")

    def warn(self, where: str, message: str) -> None:
        self.warnings.append(f"{where}: {message}")


# --------------------------------------------------------------------------------------------
# Text extraction: which fields are Basque, and which clips need audio
# --------------------------------------------------------------------------------------------

def basque_texts(exercise: dict) -> list[str]:
    """All Basque strings of an exercise (used for the vocabulary check)."""
    t = exercise["type"]
    texts: list[str] = []
    if t in ("translate_wordbank", "translate_typing"):
        if exercise["prompt"]["lang"] == "eu":
            texts.append(exercise["prompt"]["text"])
        else:
            texts += exercise["answers"] + exercise.get("distractors", [])
    elif t == "choose_image":
        texts += [option["text"] for option in exercise["options"]]
    elif t == "match_pairs":
        texts += [pair["eu"] for pair in exercise["pairs"]]
    elif t == "listen_tap":
        texts += [exercise["text"]] + exercise.get("distractors", [])
    elif t == "listen_type":
        texts += [exercise["text"]] + exercise["answers"]
    elif t == "fill_blank":
        texts += [exercise["sentence"].replace("___", " ")] + exercise["options"]
    elif t == "choose_meaning":
        texts.append(exercise["text"])
    elif t == "complete_chat":
        texts += [message["text"] for message in exercise["messages"]] + exercise["options"]
    elif t == "read_true_false":
        texts.append(exercise["passage"])
    elif t in ("order_sentence",):
        texts.append(exercise["answer"])
    elif t == "speak":
        texts.append(exercise["text"])
    return texts


def audio_clips(exercise: dict) -> list[tuple[str, str]]:
    """(audio id, Basque text) pairs an exercise may play. See docs/content-guide.md."""
    t, ex_id = exercise["type"], exercise["id"]
    if t in ("translate_wordbank", "translate_typing"):
        prompt = exercise["prompt"]
        return [(ex_id, prompt["text"] if prompt["lang"] == "eu" else exercise["answers"][0])]
    if t == "choose_image":
        return [(f"{ex_id}.{i}", option["text"]) for i, option in enumerate(exercise["options"])]
    if t == "match_pairs":
        return [(f"{ex_id}.{i}", pair["eu"]) for i, pair in enumerate(exercise["pairs"])]
    if t in ("listen_tap", "listen_type", "choose_meaning", "speak"):
        return [(ex_id, exercise["text"])]
    if t == "fill_blank":
        filled = exercise["sentence"].replace("___", exercise["options"][exercise["correct"]])
        return [(ex_id, filled)]
    if t == "complete_chat":
        clips = [(f"{ex_id}.{i}", message["text"]) for i, message in enumerate(exercise["messages"])]
        return clips + [(f"{ex_id}.answer", exercise["options"][exercise["correct"]])]
    if t == "read_true_false":
        return [(ex_id, exercise["passage"])]
    if t == "order_sentence":
        return [(ex_id, exercise["answer"])]
    return []


def tokens(text: str) -> list[str]:
    return PUNCTUATION.sub(" ", text).lower().split()


# --------------------------------------------------------------------------------------------
# Checks
# --------------------------------------------------------------------------------------------

def check_vocabulary(where: str, texts: list[str], unit: tuple[int, int], syllabus: Syllabus, report: Report) -> None:
    lemmas = syllabus.allowed(syllabus.lemmas, unit)
    forms = syllabus.allowed(syllabus.forms, unit)
    phrases = sorted((lemma for lemma in lemmas if " " in lemma), key=len, reverse=True)
    stems = [lemma for lemma in lemmas if " " not in lemma and len(lemma) >= MIN_STEM]
    for text in texts:
        normalized = " " + " ".join(tokens(text)) + " "
        for phrase in phrases:
            normalized = normalized.replace(f" {phrase} ", " ")
        for token in normalized.split():
            if token in lemmas or token in forms or token in syllabus.proper_nouns or token.isdigit():
                continue
            if any(token.startswith(stem) and len(token) - len(stem) <= MAX_SUFFIX for stem in stems):
                continue
            # Declined names: Bilbon, Gasteizen, Donostiako, Aneren.
            if any(token.startswith(name) and len(token) - len(name) <= MAX_SUFFIX
                   for name in syllabus.proper_nouns if len(name) >= MIN_STEM):
                continue
            later = syllabus.lemmas.get(token) or syllabus.forms.get(token)
            if later:
                report.error(where, f"'{token}' is taught later (unit {later[0]}.{later[1]})")
            else:
                report.error(where, f"'{token}' is not in the syllabus up to this unit")


def check_declared_vocabulary(where: str, exercise: dict, unit: tuple[int, int], syllabus: Syllabus, report: Report) -> None:
    for lemma in exercise.get("vocabulary", []):
        key = syllabus.lemmas.get(clean_lemma(lemma))
        if key is None:
            report.error(where, f"declared lemma '{lemma}' is not in vocabulary.csv")
        elif key > unit:
            report.error(where, f"declared lemma '{lemma}' belongs to unit {key[0]}.{key[1]}")


def check_exercise(where: str, exercise: dict, assets: Path, report: Report) -> None:
    t = exercise["type"]
    for key in ("options",):
        if "correct" in exercise and key in exercise and exercise["correct"] >= len(exercise[key]):
            report.error(where, f"'correct' ({exercise['correct']}) is out of range")
    for key in ("answers", "options"):
        values = exercise.get(key)
        if values and t != "choose_image" and len({v.lower() for v in values}) != len(values):
            report.error(where, f"duplicated {key}")
    if t == "fill_blank" and exercise["sentence"].count("___") != 1:
        report.error(where, "the sentence must contain exactly one '___'")
    if t == "translate_wordbank":
        answer_tokens = set(tokens(exercise["answers"][0]))
        clashes = answer_tokens & {d.lower() for d in exercise.get("distractors", [])}
        if clashes:
            report.error(where, f"distractors also appear in the answer: {sorted(clashes)}")
    if t == "order_sentence" and len(tokens(exercise["answer"])) < 2:
        report.error(where, "a sentence to order needs at least two words")
    if t == "match_pairs":
        for side in ("eu", "es"):
            values = [pair[side].lower() for pair in exercise["pairs"]]
            if len(set(values)) != len(values):
                report.error(where, f"duplicated '{side}' items in pairs")
    if t == "choose_image":
        if exercise["correct"] >= len(exercise["options"]):
            report.error(where, f"'correct' ({exercise['correct']}) is out of range")
        for option in exercise["options"]:
            if not (assets / "images" / option["image"]).exists():
                report.error(where, f"missing image images/{option['image']}")


class Ids:
    def __init__(self, report: Report) -> None:
        self.seen: dict[str, str] = {}
        self.report = report

    def add(self, item_id: str, parent: str | None, where: str) -> None:
        if item_id in self.seen:
            self.report.error(where, f"duplicated id '{item_id}' (also in {self.seen[item_id]})")
        self.seen[item_id] = where
        if parent is not None and not item_id.startswith(parent + "."):
            self.report.error(where, f"id '{item_id}' must start with '{parent}.'")


def load_json(path: Path, report: Report) -> dict | None:
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        report.error(str(path), f"invalid JSON: {exc}")
        return None


def unit_schema_errors(unit_schema: dict, unit: dict) -> list[str]:
    """Schema errors for a unit, validating each level and exercise against the branch selected by
    its ``type`` so messages point at the real problem instead of a generic ``oneOf`` failure."""
    from jsonschema import Draft202012Validator

    defs = unit_schema["$defs"]

    def run(schema: dict, instance: object, prefix: str) -> list[str]:
        out = []
        for error in Draft202012Validator(schema).iter_errors(instance):
            path = "/".join(str(p) for p in error.absolute_path)
            location = "/".join(part for part in (prefix, path) if part) or "<root>"
            out.append(f"{location}: {error.message}")
        return out

    # 1. Unit structure, with levels left opaque.
    shallow_defs = {**defs, "level": {"type": "object"}}
    messages = run({**unit_schema, "$defs": shallow_defs}, unit, "")
    if messages:
        return messages

    level_branches: dict[str, dict] = {}
    for branch in defs["level"]["oneOf"]:
        type_schema = branch["properties"]["type"]
        for value in type_schema.get("enum", [type_schema.get("const")]):
            level_branches[value] = branch
    exercise_branches = {b["properties"]["type"]["const"]: b for b in defs["exercise"]["oneOf"]}
    opaque_exercises = {**defs, "exercise": {"type": "object"}}

    for li, level in enumerate(unit["levels"]):
        branch = level_branches.get(level.get("type"))
        if branch is None:
            messages.append(f"levels/{li}/type: unknown level type {level.get('type')!r}")
            continue
        # 2. Level structure, with exercises left opaque.
        level_messages = run({**branch, "$defs": opaque_exercises}, level, f"levels/{li}")
        messages += level_messages
        if level_messages or level["type"] != "lesson":
            continue
        # 3. Each exercise against its own type.
        for si, lesson in enumerate(level["lessons"]):
            for ei, exercise in enumerate(lesson["exercises"]):
                location = f"levels/{li}/lessons/{si}/exercises/{ei}"
                ex_branch = exercise_branches.get(exercise.get("type"))
                if ex_branch is None:
                    messages.append(f"{location}/type: unknown exercise type {exercise.get('type')!r}")
                else:
                    messages += run({**ex_branch, "$defs": defs}, exercise, location)
    return messages


def validate(
    assets: Path = DEFAULT_ASSETS,
    content: Path = DEFAULT_CONTENT,
    only: set[str] | None = None,
) -> tuple[Report, list[tuple[str, str, str]]]:
    """Validates all courses (or only the units in [only]). Returns the report and the clips without audio."""
    from jsonschema import Draft202012Validator

    report = Report()
    pending: list[tuple[str, str, str]] = []
    syllabus = Syllabus.load(content)
    course_schema = json.loads((content / "schema" / "course.schema.json").read_text(encoding="utf-8"))
    unit_schema = json.loads((content / "schema" / "unit.schema.json").read_text(encoding="utf-8"))
    course_validator = Draft202012Validator(course_schema)
    audio_dir = assets / "audio"
    recorded = {p.stem for p in audio_dir.glob(f"*{AUDIO_EXTENSION}")} if audio_dir.exists() else set()

    for course_dir in sorted((assets / "courses").iterdir()):
        course_path = course_dir / "course.json"
        course = load_json(course_path, report)
        if course is None:
            continue
        for message in sorted({f"{'/'.join(map(str, e.absolute_path)) or '<root>'}: {e.message}" for e in course_validator.iter_errors(course)}):
            report.error(str(course_path.relative_to(assets)), message)
        if report.errors:
            continue
        ids = Ids(report)
        listed = []
        for section in course["sections"]:
            ids.add(section["id"], None, f"{course['id']}/course.json")
            listed += [(unit_id, section["id"]) for unit_id in section["units"]]
        files = {p.stem for p in (course_dir / "units").glob("*.json")}
        for unit_id in sorted(files - {u for u, _ in listed}):
            report.error(f"{course['id']}/units/{unit_id}.json", "unit is not listed in course.json")
        if len({u for u, _ in listed}) != len(listed):
            report.error(f"{course['id']}/course.json", "a unit is listed twice")

        if only is not None:
            # A partial run also checks units being written that course.json does not list yet.
            sections = dict(listed)
            listed = [(u, sections.get(u)) for u in sorted(only)]
        for unit_id, section_id in listed:
            where = f"{course['id']}/units/{unit_id}.json"
            path = course_dir / "units" / f"{unit_id}.json"
            if not path.exists():
                report.error(where, "listed in course.json but the file does not exist")
                continue
            unit = load_json(path, report)
            if unit is None:
                continue
            messages = unit_schema_errors(unit_schema, unit)
            for message in sorted(set(messages)):
                report.error(where, message)
            if messages:
                continue
            if unit["id"] != unit_id:
                report.error(where, f"id '{unit['id']}' does not match the file name")
            if section_id is not None and unit["section"] != section_id:
                report.error(where, f"section '{unit['section']}' but listed under '{section_id}'")
            key = unit_key_from_id(unit_id)
            if key[0] != unit["bookUnit"]:
                report.error(where, f"bookUnit {unit['bookUnit']} does not match the id")
            ids.add(unit_id, None, where)
            app_unit = f"{key[0]}.{key[1]}"

            clips: list[tuple[str, str]] = []
            for si, gb in enumerate(unit["guidebook"]["sections"]):
                examples = gb.get("examples", [])
                check_vocabulary(f"{where} guidebook {si}", [e["eu"] for e in examples], key, syllabus, report)
                clips += [(f"{unit_id}.gb{si}.{i}", e["eu"]) for i, e in enumerate(examples)]

            for level in unit["levels"]:
                ids.add(level["id"], unit_id, where)
                if level["type"] == "lesson":
                    for lesson in level["lessons"]:
                        ids.add(lesson["id"], level["id"], where)
                        for exercise in lesson["exercises"]:
                            ex_where = f"{where} {exercise['id']}"
                            ids.add(exercise["id"], lesson["id"], ex_where)
                            check_exercise(ex_where, exercise, assets, report)
                            check_declared_vocabulary(ex_where, exercise, key, syllabus, report)
                            check_vocabulary(ex_where, basque_texts(exercise), key, syllabus, report)
                            clips += audio_clips(exercise)
                elif level["type"] == "story":
                    story = level["story"]
                    ids.add(story["id"], level["id"], where)
                    st_where = f"{where} {story['id']}"
                    for question in story["questions"]:
                        if question["afterLine"] >= len(story["lines"]):
                            report.error(st_where, f"question after line {question['afterLine']} is out of range")
                        if question["correct"] >= len(question["options"]):
                            report.error(st_where, "question 'correct' is out of range")
                    check_vocabulary(st_where, [line["eu"] for line in story["lines"]], key, syllabus, report)
                    clips += [(f"{story['id']}.{i}", line["eu"]) for i, line in enumerate(story["lines"])]

            pending += [(clip_id, text, app_unit) for clip_id, text in clips if clip_id not in recorded]
            known = {clip_id for clip_id, _ in clips}
            for orphan in sorted(recorded - known):
                if orphan.startswith(unit_id + "."):
                    report.warn(where, f"audio/{orphan}{AUDIO_EXTENSION} is not used by any exercise")
    return report, pending


def pending_csv(pending: list[tuple[str, str, str]]) -> str:
    out = io.StringIO()
    writer = csv.writer(out, lineterminator="\n")
    writer.writerow(["id", "text", "unit"])
    writer.writerows(pending)
    return out.getvalue()


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.split("\n\n")[0])
    parser.add_argument("--write", action="store_true", help="regenerate content/audio/pending.csv")
    parser.add_argument("--assets", type=Path, default=DEFAULT_ASSETS)
    parser.add_argument("--content", type=Path, default=DEFAULT_CONTENT)
    parser.add_argument("--only", help="comma-separated unit ids to validate (skips the pending.csv check)")
    args = parser.parse_args(argv)

    only = set(args.only.split(",")) if args.only else None
    report, pending = validate(args.assets, args.content, only)
    csv_path = args.content / "audio" / "pending.csv"
    expected = pending_csv(pending)
    if only is not None:
        pass  # a partial run cannot tell whether pending.csv is complete
    elif args.write:
        csv_path.parent.mkdir(parents=True, exist_ok=True)
        csv_path.write_text(expected, encoding="utf-8")
    elif not csv_path.exists() or csv_path.read_text(encoding="utf-8") != expected:
        report.errors.append(f"{csv_path.relative_to(ROOT) if csv_path.is_relative_to(ROOT) else csv_path}: "
                             "out of date; run tools/validate_content.py --write")

    for warning in report.warnings:
        print(f"WARNING {warning}")
    for error in report.errors:
        print(f"ERROR   {error}")
    print(f"{len(report.errors)} error(s), {len(report.warnings)} warning(s), {len(pending)} clip(s) pending recording")
    return 1 if report.errors else 0


if __name__ == "__main__":
    sys.exit(main())
