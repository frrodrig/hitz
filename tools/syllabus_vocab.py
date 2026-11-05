#!/usr/bin/env python3
"""Regenerates the vocabulary tables in docs/syllabus.md from
content/syllabus/vocabulary.csv.

Tables are written between the markers
<!-- vocab:<app_unit>:start --> and <!-- vocab:<app_unit>:end -->.
Usage: python3 tools/syllabus_vocab.py [--check]
With --check it fails (exit 1) if the document is out of sync or if a
section falls outside the 100–150 core-word range.
"""
from __future__ import annotations

import collections
import csv
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
CSV_PATH = ROOT / "content" / "syllabus" / "vocabulary.csv"
DOC_PATH = ROOT / "docs" / "syllabus.md"
MIN_CORE, MAX_CORE = 100, 150

POS = {
    "n": "noun", "v": "verb", "adj": "adj.", "adv": "adv.", "pron": "pron.",
    "interj": "interj.", "expr": "phrase", "conj": "conj.", "postp": "postp.",
    "num": "num.",
}


def load() -> list[dict[str, str]]:
    with CSV_PATH.open(encoding="utf-8") as fh:
        return list(csv.DictReader(fh))


def table(rows: list[dict[str, str]]) -> str:
    core = [r for r in rows if r["set"] == "core"]
    closed = [r for r in rows if r["set"] == "closed"]
    function = [r for r in rows if r["set"] == "function"]
    out = [f"**Vocabulary ({len(core)} words)**", "",
           "| Basque | Spanish | Type | Note |", "|---|---|---|---|"]
    out += [f"| {r['lemma']} | {r['es']} | {POS.get(r['pos'], r['pos'])} | {r['note']} |"
            for r in core]
    if closed:
        out += ["", "**Closed set (not counted towards the limit):** "
                + ", ".join(f"{r['lemma']} ({r['es']})" for r in closed)]
    if function:
        out += ["", "**Function words and survival phrases (not counted towards the limit):** "
                + ", ".join(f"{r['lemma']} ({r['es']})" for r in function)]
    return "\n".join(out)


def render(doc: str, rows: list[dict[str, str]]) -> str:
    by_unit: dict[str, list[dict[str, str]]] = collections.defaultdict(list)
    for r in rows:
        by_unit[r["app_unit"]].append(r)

    def repl(m: re.Match[str]) -> str:
        unit = m.group(1)
        return f"<!-- vocab:{unit}:start -->\n{table(by_unit.get(unit, []))}\n<!-- vocab:{unit}:end -->"

    doc = re.sub(r"<!-- vocab:([\d.]+):start -->.*?<!-- vocab:\1:end -->", repl, doc, flags=re.S)
    counts = collections.Counter(r["section"] for r in rows if r["set"] == "core")
    summary = "\n".join(f"| {s} | {counts[s]} |" for s in sorted(counts, key=int))
    return re.sub(r"(<!-- counts:start -->\n).*?(<!-- counts:end -->)",
                  lambda m: m.group(1) + "| Section | Core words |\n|---|---|\n" + summary + "\n" + m.group(2),
                  doc, flags=re.S)


def main() -> int:
    rows = load()
    errors = []
    dup = [k for k, v in collections.Counter(r["lemma"] for r in rows).items() if v > 1]
    if dup:
        errors.append(f"duplicate lemmas: {dup}")
    counts = collections.Counter(r["section"] for r in rows if r["set"] == "core")
    for sec, n in sorted(counts.items()):
        if not MIN_CORE <= n <= MAX_CORE:
            errors.append(f"section {sec}: {n} words (expected {MIN_CORE}–{MAX_CORE})")
    doc = DOC_PATH.read_text(encoding="utf-8")
    new = render(doc, rows)
    if "--check" in sys.argv:
        if new != doc:
            errors.append("docs/syllabus.md is out of sync; run tools/syllabus_vocab.py")
    else:
        DOC_PATH.write_text(new, encoding="utf-8")
    for e in errors:
        print(f"ERROR: {e}", file=sys.stderr)
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
