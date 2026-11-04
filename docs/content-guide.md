# Content guide

> This guide is completed in M2 (content model and validator). For now it records the rules
> that already apply.

## Principles

1. **Original content.** The *Arian A1* textbook only defines the syllabus (order, goals,
   grammar and lexical fields). Texts, dialogues, exercises, word lists and images are never
   copied.
2. **Standard Basque (euskara batua).** Any doubt is flagged with `TODO(revisión-nativa)` in the
   JSON (`notes` field) or in a comment, and is not shipped until reviewed.
3. **Syllabus vocabulary only.** Each lesson uses words from its unit or from earlier units, as
   listed in `content/syllabus/vocabulary.csv`. The validator will enforce this (M2).

## Syllabus

- Data source: `content/syllabus/vocabulary.csv`, with the columns `lemma`, `es`, `pos`,
  `section`, `book_unit`, `app_unit`, `set` (`core` or `closed`) and `note`.
- `docs/syllabus.md` is regenerated with `python3 tools/syllabus_vocab.py`. CI checks that it is
  in sync (`--check`) and that every section has 100–150 core words.
- Closed sets (numbers, weekdays and months) use `set=closed` and do not count towards that
  limit.

## Layout (planned for M2)

```
app/src/main/assets/courses/eu-es/   Course JSON (Course > Section > Unit > Level > Lesson > Exercise)
app/src/main/assets/audio/           Pre-recorded audio, named by id
content/schema/                      JSON Schema for the content
content/syllabus/                    Syllabus as data
tools/                               Validators and generators
```
