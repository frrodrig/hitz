# AGENTS.md

Guide for AI coding agents (and humans in a hurry) working on **Hitz**, an Android app to learn
Basque (euskara batua, A1) from Spanish. Read this first, then the doc for the area you touch:
[`docs/architecture.md`](docs/architecture.md), [`docs/design-system.md`](docs/design-system.md),
[`docs/content-guide.md`](docs/content-guide.md), [`CONTRIBUTING.md`](CONTRIBUTING.md).

## Rules that are never broken

1. **Never copy the reference textbook.** *Arian A1* (Ikaslearen liburua, Lan-koadernoa) only
   defines the syllabus: order, goals, grammar and lexical fields. Texts, dialogues, exercises,
   word lists and images are always original. The books (PDFs) are never committed (`*.pdf` and
   `reference/` are git-ignored).
2. **Correct euskara batua.** Mark any linguistic doubt with `TODO(revisión-nativa)`; do not ship
   it as settled. Content may only use vocabulary taught in that unit or earlier (the validator
   enforces it).
3. **Original identity.** Never use "Duolingo" (code, UI, docs, store text) or any of its assets,
   sounds, texts, mascot or brand. The cast (Ane, Mikel, Maite, Unai), palette and icons are
   Hitz's own.
4. **No secrets and nothing paid.** No keys, tokens or keystores in the repository (signing
   lives in GitHub Secrets); no paid services or SDKs. The app is offline-first: its only network
   request is the daily update check (see `docs/architecture.md`, *Updates*). Do not add others
   without asking.
5. **Ask before anything irreversible or ambiguous** (publishing a release, deleting data,
   rewriting history, a product decision with several reasonable answers). Otherwise decide,
   say why in one line, and carry on.

## Languages

- Code, comments, docs, commit messages and PR descriptions: **English**.
- User-facing strings: Spanish in `values/strings.xml` (default) **and** Basque in
  `values-eu/strings.xml`. Every new string goes in both; Basque you are unsure of gets
  `<!-- TODO(revisión-nativa) -->`.
- Course content: Basque, with Spanish for prompts and hints.

## Map of the repository

```
app/                      MainActivity, root navigation (HitzApp), update dialog
feature/onboarding|path|lesson|practice|profile
                          One feature per module: screens, ViewModels, navigation routes
core/designsystem/        Theme (Material 3 Expressive), cast, icons, shared components
core/data/                Repositories: Room, DataStore, assets (course JSON, audio, images)
core/domain/              Use cases and repository interfaces, pure Kotlin
core/model/               Content and domain model, pure Kotlin
core/testing/             Test doubles shared by tests
content/                  Course authoring source (YAML), schemas, syllabus CSVs, audio lists
tools/                    Python: build and validate content, syllabus tables, art, audio, README images
build-logic/              Gradle convention plugins (hitz.android.*, hitz.jvm.library, hitz.hilt…)
docs/                     Architecture, design system, content guide, syllabus, design boards
.github/                  Workflows (ci, ui-tests, voices, release, pr-title) and their scripts
```

Dependency rules: `feature:*` → `core:designsystem`, `core:domain`, `core:model` (never another
feature, never `core:data`); `core:data` implements `core:domain`; only `:app` sees everything.

## Commands

```bash
./gradlew assembleDebug                 # build
./gradlew detekt                        # style: detekt + ktlint rules, whole repo, from the root
./gradlew lintDebug                     # Android Lint
./gradlew testDebugUnitTest             # unit tests, Robolectric UI tests, Roborazzi verify
./gradlew :feature:lesson:testDebugUnitTest --tests '*LessonViewModelTest*'   # one test class
./gradlew recordRoborazziDebug          # re-record reference screenshots locally

pip install -r tools/requirements.txt
python3 tools/build_content.py          # content/source YAML -> assets JSON (--check in CI)
python3 tools/validate_content.py       # schema, ids, vocabulary, audio and image references
python3 tools/syllabus_vocab.py         # tables of docs/syllabus.md (--check in CI)
python3 -m unittest discover -s tools/tests
python3 tools/audio/cast_voices.py check   # is the bundled course audio current?
python3 tools/design/cast_to_vector.py     # cast SVGs -> vectors + CastArt.kt (--check in tests)
python3 tools/readme_images.py          # README screenshots and cast picture
```

CI (`ci.yml`) runs all the checks above on every PR; make them pass locally first when you can.
Gradle errors are also published as check annotations
(`gh api repos/<owner>/<repo>/check-runs/<id>/annotations`), which is the quickest way to read a
failure.

## How code is written here

- **Architecture**: Clean Architecture, MVVM/UDF. A screen = `XxxScreen` (stateful, gets the
  ViewModel) + `XxxContent` (stateless, previewable, tested) + `XxxViewModel` exposing one
  immutable `StateFlow<XxxUiState>`; one-off events through a `Channel`. Logic goes in
  `core:domain` use cases (`operator fun invoke`), injected with Hilt.
- **UI**: Compose with Material 3 Expressive components. Never hard-code colours, fonts, sizes or
  icons: use `MaterialTheme`, `HitzTheme.extendedColors`, `HitzSpacing`, `HitzIcons` and the
  `Hitz*` components. Every component gets a `@HitzPreviews` preview.
- **Accessibility is part of done**: TalkBack labels and roles, 48 dp touch targets, 200 % font
  scale, reduced motion (`rememberReducedMotion()`), dark theme and wide windows (navigation rail).
- **Navigation**: type-safe `@Serializable` routes in each feature's `navigation/` package.
- **Resources**: library modules prefix resources with the module name
  (`feature_lesson_…`, `core_designsystem_…`).
- **Style**: detekt with ktlint rules; lines ≤ 120; at most 3 returns per function (guard clauses
  aside); constructors ≤ 8 parameters. Prefer `--auto-correct` for formatting.
- **Tests**: JUnit 4, Truth, Turbine, coroutines-test. Domain and model tests are plain JVM; UI
  tests use Robolectric with `createComposeRule` and capture Roborazzi screenshots
  (`src/test/screenshots/`). Infinite animations are paused in Compose tests, so animated
  composables must look right at rest.
- **Build quirks**: AGP 9 with built-in Kotlin (do not apply `org.jetbrains.kotlin.android`);
  depend on modules with `project(":…")`, not type-safe accessors; `material3` is pinned to
  1.5.0 in the catalog for the Expressive APIs.
- **Dependencies**: stable versions only (no alpha, beta or RC) unless a feature cannot be built
  without one; then pin it, say why in the PR and in the docs, and move back to stable as soon
  as it ships.

## Content, audio and art

- **Course content** is authored in `content/source/` (YAML) and built into
  `core/data/src/main/assets/courses/eu-es/` with `tools/build_content.py`; never edit the JSON by
  hand. Read `docs/content-guide.md` before writing lessons.
- **Audio** is synthesised with Piper and the HiTZ – Aholab Basque voices
  (`tools/audio/cast_voices.py`): Maider for Ane and Maite, Antton for Mikel and Unai; dialogue
  speakers' voices are in `content/audio/speakers.json` (a new speaker must be added there). When
  a branch changes the course, the **Voices** workflow regenerates the changed clips and opens a
  PR against that branch; merge it, or CI's audio check fails. Human recordings
  (`tools/audio/import_recordings.py`) take precedence over synthetic clips.
- **Cast art** lives in `tools/design/art/cast/*.svg` and the star challenges' scenes in
  `tools/design/art/scenes/<unit>.svg`; `data-motion` / `data-pivot` drive the animation. After
  editing, run `tools/design/add_depth.py` (shading) and then `tools/design/cast_to_vector.py`;
  never edit the generated vectors, `CastArt.kt` or `SceneArt.kt`.
- **Exercise pictures** are drawn as code in `tools/design/illustrations/`
  (`make_illustrations.py`, needs `rsvg-convert`).

## Branches, PRs and screenshots

- Never commit to `main`; branch as `feat/…`, `fix/…`, `content/…`, `docs/…`, `ci/…`, `build/…`,
  `test/…`. PRs are squash-merged, so the **PR title** is the commit and must follow Conventional
  Commits (`feat(lesson): match-pairs exercise`); the *PR title* workflow checks it.
- Fill in the PR template; describe the change and how it was tested.
- **Screenshots**: when a UI change is intentional, run the **UI tests** workflow on the branch
  with `record_screenshots`; it opens a PR with the new references against your branch. Review
  the images, merge that PR, then the feature PR. If the change shows in the README screenshots,
  run `python3 tools/readme_images.py` afterwards.
- Keep `README.md` and the docs in `docs/` true to the code in the same PR that changes it.

## Releases

Releases are made by tagging `main` with `vMAJOR.MINOR.PATCH`: the **Release** workflow builds
and signs the APK and AAB, smoke-tests the minified APK on an emulator and publishes a GitHub
Release with a changelog. Publishing is public and installed apps offer it as an update, so an
agent creates a tag only when asked to. Details in `CONTRIBUTING.md`.
