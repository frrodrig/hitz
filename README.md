# Hitz

[![CI](https://github.com/frrodrig/hitz/actions/workflows/ci.yml/badge.svg)](https://github.com/frrodrig/hitz/actions/workflows/ci.yml)

Android app for learning **Basque (euskara) from Spanish** at A1 level: a path of short lessons,
spaced repetition, a daily goal in minutes and a score on the CEFR A1 scale (0–29). The mascot
is a **Galtzagorri**, one of the tireless little red-trousered helpers of Basque mythology.

- Made for learning on your own from zero: a first-run introduction, an introduction to every
  unit, a "new word" card before each word is used and tap hints on every Basque word. Learners
  who already know some Basque can take a placement test to skip sections.
- 100 % offline, no paid backend.
- UI available in Spanish and Basque.
- All course content is original, illustrations included (drawn as code in
  `tools/design/illustrations/`). The syllabus follows the curricular order of the *Arian A1*
  textbook (see [`docs/syllabus.md`](docs/syllabus.md)).

## Status

| Milestone | Scope | Status |
|---|---|---|
| M0 | Repo, build-logic and CI | ✅ |
| M1 | Design system and theme | ✅ |
| M2 | Content model and validator | ✅ |
| M3 | Lesson engine and 4 exercise types | ✅ |
| M4 | Learning path and progress | ✅ |
| M5 | Remaining exercise types and stories | ✅ |
| M6 | Gamification | ✅ |
| M7 | Full A1 content | ✅ |
| M8 | Audio, accessibility and polish | ✅ |
| M9 | Release | ⏳ |

## Install

Download `hitz-<version>.apk` from [Releases](https://github.com/frrodrig/hitz/releases) and open
it on an Android 8.0+ phone (allow installing from that source when asked). Each release also
carries the `.aab` bundle for app stores. See [`CONTRIBUTING.md`](CONTRIBUTING.md#releases) for how
releases are made.

## Building

Requirements: JDK 17 and the Android SDK (API 37).

```bash
./gradlew assembleDebug          # debug APK
./gradlew detekt                 # style (detekt + ktlint rules)
./gradlew lintDebug              # Android Lint
./gradlew testDebugUnitTest      # unit tests
./gradlew recordRoborazziDebug   # re-record reference screenshots
python3 tools/syllabus_vocab.py  # regenerate the syllabus tables
python3 tools/validate_content.py # validate course content (pip install -r tools/requirements.txt)
python3 tools/design/make_illustrations.py # re-render the pictures (needs rsvg-convert)
```

Every PR produces a debug APK as an artifact of the **CI** workflow.

## Documentation

- [`docs/architecture.md`](docs/architecture.md): modules, layers and technical decisions.
- [`docs/design-system.md`](docs/design-system.md): colours, type, icons and components.
- [`docs/content-guide.md`](docs/content-guide.md): how content is written and validated.
- [`docs/syllabus.md`](docs/syllabus.md): syllabus (units, goals, grammar and vocabulary).
- [`CONTRIBUTING.md`](CONTRIBUTING.md): branches, commits and releases.

## License

[Apache License 2.0](LICENSE), for both code and course content.
