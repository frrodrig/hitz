# Hitz

[![CI](https://github.com/frrodrig/hitz/actions/workflows/ci.yml/badge.svg)](https://github.com/frrodrig/hitz/actions/workflows/ci.yml)

Android app for learning **Basque (euskara) from Spanish** at A1 level: a path of short lessons,
spaced repetition and gamification. The mascot is a **Galtzagorri**, one of the tireless little
red-trousered helpers of Basque mythology.

- 100 % offline, no paid backend. Leagues are simulated on the device.
- UI available in Spanish and Basque.
- All course content is original. The syllabus follows the curricular order of the *Arian A1*
  textbook (see [`docs/syllabus.md`](docs/syllabus.md)).

## Status

| Milestone | Scope | Status |
|---|---|---|
| M0 | Repo, build-logic and CI | ✅ |
| M1 | Design system and theme | ⏳ |
| M2 | Content model and validator | ⏳ |
| M3 | Lesson engine and 4 exercise types | ⏳ |
| M4 | Learning path and progress | ⏳ |
| M5 | Remaining exercise types | ⏳ |
| M6 | Gamification | ⏳ |
| M7 | Full A1 content | ⏳ |
| M8 | Audio, accessibility and polish | ⏳ |
| M9 | Release | ⏳ |

## Building

Requirements: JDK 17 and the Android SDK (API 37).

```bash
./gradlew assembleDebug          # debug APK
./gradlew detekt                 # style (detekt + ktlint rules)
./gradlew lintDebug              # Android Lint
./gradlew testDebugUnitTest      # unit tests
./gradlew recordRoborazziDebug   # re-record reference screenshots
python3 tools/syllabus_vocab.py  # regenerate the syllabus tables
```

Every PR produces a debug APK as an artifact of the **CI** workflow.

## Documentation

- [`docs/architecture.md`](docs/architecture.md): modules, layers and technical decisions.
- [`docs/content-guide.md`](docs/content-guide.md): how content is written and validated.
- [`docs/syllabus.md`](docs/syllabus.md): syllabus (units, goals, grammar and vocabulary).
- [`CONTRIBUTING.md`](CONTRIBUTING.md): branches, commits and releases.

## License

[Apache License 2.0](LICENSE), for both code and course content.
