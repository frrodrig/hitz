# Architecture

## Modules

```
:app                       Activity, root navigation, Hilt application
:feature:path              Learning path (nodes, sections, unit guidebook)
:feature:lesson            Lesson engine and exercises
:feature:profile           Profile, streak, achievements
:feature:leaderboard       Locally simulated leagues
:feature:shop              Gem shop
:core:designsystem         Material 3 theme, components and previews (see design-system.md)
:core:data                 Repositories, Room, DataStore, bundled course content (assets/)
:core:domain               Use cases and repository interfaces (pure Kotlin)
:core:model                Domain model (pure Kotlin)
```

Dependency rules:

- `feature:*` → `core:designsystem`, `core:domain`, `core:model`. A feature never depends on
  another feature or on `core:data`.
- `core:data` → `core:domain` (implements its interfaces) → `core:model`.
- `:app` wires everything together: it is the only module that knows `core:data` and every
  feature.
- `core:model` and `core:domain` are JVM modules without Android, so their tests are fast and
  need no Robolectric.

## Patterns

- **UDF / MVVM**: each screen has a `ViewModel` exposing an immutable `StateFlow<UiState>` and
  receiving actions (`onAction(Action)`). One-off events (navigation, snackbars) go through a
  `Channel` exposed as a `Flow`.
- **Content as data**: course JSON in `core:data` assets, decoded into the `core:model` content
  model and served by `CourseRepository` (see `docs/content-guide.md`).
- **Use cases** in `core:domain` (`operator fun invoke`), injected with Hilt.
- **Offline-first**: the source of truth is local (Room and DataStore). `SyncRepository` is the
  seam for a future backend; today `OfflineSyncRepository` returns `SyncResult.Disabled`.
- **Type-safe navigation** with `@Serializable` routes defined by each feature
  (`feature/*/navigation`).

## Lesson engine

The rules live in `core:domain` (`eus.hitz.core.domain.lesson`) as pure Kotlin, so they are fully
unit-tested without Android:

| Piece | Responsibility |
|---|---|
| `AnswerNormalizer` | Lower case, no vowel accents, no punctuation, single spaces (`ñ` is kept). |
| `AnswerChecker` | Any accepted answer is correct; a small typo (optimal string alignment distance ≤ 1, or ≤ 2 from 12 letters; none under 4 letters; same number of words) is *almost correct* and shows the right spelling. |
| `WordBank` | Tiles = canonical answer words + distractors, shuffled with a per-exercise seed. Tile answers must be exact (an "almost" tile answer is a wrong order). |
| `MatchPairsState` | Pair selection; wrong pairs are counted and recorded as mistakes but cost no heart. |
| `LessonSession` | Queue: a wrong answer costs a heart and the exercise is asked again at the end until it is right; progress only grows with correct answers; no hearts left ends the lesson. Produces a `LessonResult` with first-try accuracy and the `Mistake`s (by word and structure) for spaced repetition. |

`feature:lesson` maps the session to an immutable `LessonUiState` in `LessonViewModel`
(`StateFlow` + `LessonAction`s in, one-off `LessonEvent`s out). `LessonContent` is stateless and is
what previews, UI tests and screenshot tests render. Exercise types not yet supported by the UI are
skipped (M3 supports word bank, typing, choose image and match pairs).

## Learning path and progress

- **Progress** is stored in Room (`HitzDatabase`, `core:data`): `completions` (a lesson id, or the
  level id of a review/legendary level, with best first-try accuracy) and `mistakes` with
  `mistake_tags` (`lemma` / `structure`). Room schemas are exported to `core/data/schemas/` and
  committed by the screenshot-recording workflow.
- **Path rules** (`LearningPathBuilder`, pure Kotlin): progression is linear over the required
  levels (lessons and unit reviews); a lesson level is complete when all its lessons are; stories and
  legendary levels are optional and never block. Exactly one node is *current*.
- **Sessions** (`PlanSessionUseCase`): lesson levels play the next lesson; the **review** picks the
  10 unit exercises with the highest mistake score (3 × mistakes on the exercise + mistakes on its
  lemmas + mistakes on its structures), so spaced repetition follows the learner's weak words and
  grammar; the **legendary** level plays every unit exercise shuffled with 3 hearts.
  `RecordSessionUseCase` saves mistakes always and the completion only when the session is finished.
- `feature:path` shows sections, unit headers with the guidebook, and nodes whose Material
  expressive shape depends on the level type (lesson, story, review, legendary).

## Build

- Version catalog in `gradle/libs.versions.toml`.
- Convention plugins in `build-logic/convention`:

| Plugin | Applies |
|---|---|
| `hitz.android.application` | AGP application, SDK levels, Java 17, Lint |
| `hitz.android.application.compose` | + Compose, Material 3, Robolectric/Roborazzi for tests |
| `hitz.android.library` | AGP library, SDK levels, per-module resource prefix |
| `hitz.android.library.compose` | + Compose and Roborazzi |
| `hitz.android.feature` | Compose library + Hilt + Navigation + core dependencies |
| `hitz.android.room` | Room + KSP with exported schemas |
| `hitz.hilt` | Hilt + KSP (only `hilt-core` in JVM modules) |
| `hitz.jvm.library` | Pure Kotlin JVM |

- AGP 9 with built-in Kotlin: `org.jetbrains.kotlin.android` is not applied.
- UI: Material 3 Expressive (`material3` 1.5.0 pre-release pinned in the catalog; see
  `docs/design-system.md`).
- Project dependencies use `project(":…")`: type-safe project accessors break on a module
  named `path` (it clashes with `ProjectDependency.getPath()`).
- Style: **detekt** with `detekt-formatting` (ktlint rules) runs once from the root project over
  all sources, so it does not depend on the variant API removed in AGP 9.

## CI/CD (GitHub Actions, free tier)

| Workflow | Trigger | What it does |
|---|---|---|
| `ci.yml` | PRs and pushes to `main` | Syllabus check, content validator and its tests, detekt, Lint, unit tests, debug build, APK artifact |
| `ui-tests.yml` | Manual and nightly | Roborazzi (verify, or record and open a PR) and instrumented tests on an emulator |
| `release.yml` | `v*` tag | Signed AAB and APK, GitHub Release with generated changelog |
| `pr-title.yml` | PRs | Conventional Commits title |

Gradle failures are re-published as check annotations by `.github/scripts/gradlew-annotated.sh`,
so they are readable from the PR checks tab without downloading logs. Every workflow uses
`concurrency` with cancellation and the Gradle cache (written only on `main`). The repository is
public, so standard runner minutes are unlimited.
