# Content guide

How course content is structured, written and validated.

## Principles

1. **Original content.** The *Arian A1* textbook only defines the syllabus (order, goals, grammar
   and lexical fields). Texts, dialogues, exercises, word lists and images are never copied.
2. **Standard Basque (euskara batua).** Flag any doubt with `TODO(revisión-nativa)` and do not
   ship it until a native speaker has reviewed it.
3. **Syllabus vocabulary only.** A unit may only use words taught in that unit or earlier. The
   validator enforces this.

## Layout

```
content/source/
  course.yaml                        Course index (sections and their units, in path order)
  units/<unit>.yaml                  Authoring source of each unit (see "Writing a unit")
core/data/src/main/assets/
  courses/eu-es/course.json          Generated from content/source by tools/build_content.py
  courses/eu-es/units/<unit>.json    Generated: one file per unit, guidebook and levels
  images/<name>.webp                 Images for "choose the image"
  audio/<clip-id>.ogg                Pre-recorded audio (see "Audio")
content/
  schema/course.schema.json          JSON Schema for course.json
  schema/unit.schema.json            JSON Schema for unit files
  syllabus/vocabulary.csv            Syllabus lemmas, by app unit (see docs/syllabus.md)
  syllabus/forms/<unit>.csv          Inflected forms and grammar words each unit teaches
  syllabus/proper-nouns.csv          Names of people and places allowed anywhere
  audio/pending.csv                  Generated list of clips still to be recorded
tools/
  build_content.py                   Builds the course JSON from content/source (CI checks it is current)
  validate_content.py                Content validator (run in CI)
  syllabus_vocab.py                  Regenerates the tables in docs/syllabus.md
```

The Kotlin model lives in `:core:model` (`eus.hitz.core.model.content`) and is decoded strictly
(`ContentJson`): an unknown field is an error, so the JSON, the schema and the model cannot drift
apart unnoticed (`BundledContentTest` decodes every bundled file).

## Model

```
Course ─ Section ─ Unit (guidebook) ─ Level ─ Lesson ─ Exercise
```

| Element | Id pattern | Notes |
|---|---|---|
| Section | `s1` | Groups three textbook units. |
| Unit | `u01-1` | `u<textbook unit>-<half>`; file `units/u01-1.json`. `bookUnit` must match. |
| Level | `u01-1.l1` | `lesson`, `story`, `review` or `legendary`. |
| Lesson | `u01-1.l1.s1` | 10–15 exercises in real content. |
| Exercise | `u01-1.l1.s1.e01` | Ids are unique and always nested in their parent's id. |
| Story | `u01-1.l3.story` | Inside a `story` level. |

`review` and `legendary` levels carry no exercises: the app builds them at run time from the
unit's exercises and the learner's mistakes (spaced repetition). Stories are required to move on
along the path; legendary levels are optional.

A story is a short dialogue: `lines[{speaker, eu, es}]` and `questions[{afterLine, prompt,
options, correct}]`, where `afterLine` is the 0-based line after which the question appears.
Questions are in Spanish and check comprehension, not translation.

Every exercise can declare `vocabulary` (syllabus lemmas it practises) and `structures` (grammar
tags such as `izan-present`). Both feed spaced repetition, so declare them carefully.

### Exercise types

| `type` | Fields | Basque fields (vocabulary check) |
|---|---|---|
| `translate_wordbank` | `prompt {lang, text}`, `answers[]`, `distractors[]` | prompt if `eu`, else answers and distractors |
| `translate_typing` | `prompt {lang, text}`, `answers[]` | same as above |
| `choose_image` | `prompt` (es), `options[{text, image}]`, `correct` | option texts |
| `match_pairs` | `pairs[{eu, es}]` (3–6) | `eu` |
| `listen_tap` | `text`, `distractors[]` | text and distractors |
| `listen_type` | `text`, `answers[]` | text and answers |
| `fill_blank` | `sentence` (with one `___`), `options[]`, `correct`, `translation` | sentence and options |
| `choose_meaning` | `text` (eu), `options[]` (es), `correct` | text |
| `complete_chat` | `messages[{speaker, text}]`, `options[]`, `correct` | messages and options |
| `read_true_false` | `passage` (eu), `statement` (es), `answer` | passage |
| `order_sentence` | `answer` (eu), `translation` | answer |
| `speak` | `text` (eu), `translation` | text |

Options of `fill_blank`, `choose_meaning` and `complete_chat` are shuffled in the app, so their
order in the JSON does not matter. `speak` exercises can be turned off by the learner, so a lesson
must still make sense without them.

Accepted answers are lists: the **first** one is canonical (it is shown as the correction and
used to build word-bank tiles). Add every natural variant, e.g. with and without the subject
pronoun (`¡Hola! Soy Ane.` / `¡Hola! Yo soy Ane.`). Case, accents and punctuation are normalized
when checking answers, so do not add variants that only differ in those.

## Vocabulary check

For each unit, the validator collects every Basque string (table above, story lines and guidebook
examples) and accepts a word when it is:

1. a lemma of `vocabulary.csv` taught in this unit or earlier (multi-word lemmas such as
   `eskerrik asko` are matched as a whole);
2. that lemma plus a suffix (`etxe` → `etxea`, `etxean`), for lemmas of three letters or more;
3. a form listed in `forms/<unit>.csv` for this unit or earlier (verb forms like `naiz`, short
   words like `nire`, grammar words like `ala`);
4. a name in `proper-nouns.csv`, also declined (`Bilbon`, `Aneren`), or a number.

Anything else is an error, including a word that the syllabus teaches in a **later** unit. When a
natural sentence needs a new inflected form, add it to `forms/<unit>.csv` of the unit where its
grammar is taught; when it needs a new word, discuss it against the syllabus first.

## Audio

Android's TTS has little Basque support, so clips are pre-recorded and the app falls back to TTS
only when a clip is missing. Clip ids are derived from the content, so they stay stable:

| Source | Clip id | Text |
|---|---|---|
| Exercise with one Basque sentence | `<exercise id>` | prompt, text, passage, answer or filled-in sentence |
| `choose_image`, `match_pairs` | `<exercise id>.<index>` | each option / pair |
| `complete_chat` | `<exercise id>.<message index>`, `<exercise id>.answer` | each message and the right reply |
| Story | `<story id>.<line index>` | each line |
| Guidebook example | `<unit id>.gb<section>.<example>` | each example |

Put recordings in `assets/audio/<clip id>.ogg` (Opus, mono). `content/audio/pending.csv` lists the
clips that are still missing; regenerate it with `python3 tools/validate_content.py --write`. CI
fails if it is out of date.

## Images

`choose_image` options reference `images/<name>.webp` (lower-case, underscores). Images must be
original, simple and readable at 96 dp; the validator fails if a file is missing.

## Writing a unit

Units are written in YAML (`content/source/units/<unit>.yaml`; `u01-1.yaml` is the reference) and
`tools/build_content.py` generates the JSON the app reads. Authors write *material*; the script
decides the exercise types:

- **Sentences** (`Basque | Basque variant = Spanish | Spanish variant`): each becomes one exercise.
  The type rotates through translate with word bank (both ways), listen and tap, translate typing
  (both ways), order the sentence, listen and type, and speak, so a lesson mixes them all.
  Word-bank distractors come from the lesson's other sentences. A sentence can also be a mapping
  with `text`, `structures`, `kind` (force a type) or `skip` (types to avoid).
- **Hand-made items**, interleaved with the sentence exercises: `pairs` (4–6 `Basque = Spanish`),
  `blanks` (`text: … ___ … = translation` plus `options`, the first one right), `meanings`
  (`text` plus Spanish `options`, the first one right), `chats` (`Speaker | text` messages plus
  Basque `options`, the first one right), `true_false` (`passage`, `statement`, `answer`) and
  `images` (`choose_image`, only with existing images).
- Option lists are shuffled by the app, so the first one can always be the right one.
- `structures` on a lesson tags all its exercises; `vocabulary` is worked out from the syllabus.
- Levels: three lesson levels (vocabulary, grammar, communicative use), each with three lessons of
  10–15 exercises; then a `story` (lines as `Speaker | Basque = Spanish`, comprehension questions
  in Spanish), a `review` and a `legendary` level. The last unit of each section also gets a
  section review, added by the script.
- Quote a YAML string that contains `: ` or ` #`, or starts with `[ { " ' * & ! % @`. Never put a
  gap inside a multi-word expression (`egun ___`): the validator checks options on their own.

## Workflow

```bash
pip install -r tools/requirements.txt
python3 tools/build_content.py              # YAML sources → course JSON
python3 tools/validate_content.py --write   # validate and refresh pending.csv
python3 tools/validate_content.py --only u05-1   # check one unit while writing it
python3 -m unittest discover -s tools/tests # validator tests
./gradlew :core:data:testDebugUnitTest      # decode all content with the app's model
```
