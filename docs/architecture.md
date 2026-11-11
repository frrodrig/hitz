# Architecture

## Modules

```
:app                       Activity, root navigation, Hilt application
:feature:onboarding        First-run introduction and starting level
:feature:path              Learning path (nodes, sections, unit introduction and guidebook)
:feature:lesson            Lesson engine, exercises, stories and placement tests
:feature:profile           Score, daily goal and week, achievements, settings
:core:designsystem         Material 3 theme, components and previews (see design-system.md)
:core:data                 Repositories, Room, DataStore, bundled course content (assets/)
:core:domain               Use cases and repository interfaces (pure Kotlin)
:core:model                Domain model (pure Kotlin)
:core:testing              Test doubles shared by other modules' tests (testImplementation only)
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
| `MatchPairsState` | Pair selection; wrong pairs are counted and recorded as mistakes but do not count against a mistake limit. |
| `LessonSession` | Queue: a wrong answer is asked again at the end until it is right (and uses up one of the allowed mistakes when there is a limit); progress only grows with correct answers; one mistake more than allowed ends the session; a skipped exercise (speaking) leaves without counting. Produces a `LessonResult` with first-try accuracy and the `Mistake`s (by word and structure) for spaced repetition. |
| `StorySession` | Story flow: lines revealed one by one, each question after the line it refers to; a wrong option is marked and the learner tries again (no mistake limit). Accuracy = questions right at the first try. |

`feature:lesson` maps the session to an immutable `LessonUiState` in `LessonViewModel`
(`StateFlow` + `LessonAction`s in, one-off `LessonEvent`s out). `LessonContent` is stateless and is
what previews, UI tests and screenshot tests render. All twelve exercise types are supported, plus
the "new word" card:

- **Tiles** (`ExerciseUi.TileAnswer`): translate with a word bank, listen and tap, order the
  sentence. Answers must be exact.
- **Typing** (`ExerciseUi.TextAnswer`): translate by typing, listen and type. Tolerant checking.
- **Single choice** (`ExerciseUi.SingleChoice`): choose the image, fill the blank, choose the
  meaning, complete the chat, true or false. Text options are shuffled per exercise; `order` maps
  them back to the content index. True/false shows no correction (the other option is right).
- **New word** (`ExerciseUi.WordIntro`): picture, word, translation and audio; *Continue* removes
  it from the queue without counting it (`LessonSession.skip`).
- **Match pairs** finishes by itself; **speak** uses the system speech recogniser
  (`RecognizerIntent`, `eu-ES`, no microphone permission). Recognised speech is checked tolerantly;
  a failed attempt shows the sentence but is not a mistake, and *"I can't speak now"* skips the
  remaining speaking exercises of the session. Speaking can be turned off in the profile
  (`SettingsRepository`, Preferences DataStore), which leaves them out of every session.

**Audio**: `AudioProvider` (`core:domain`) plays `assets/audio/<clip id>.ogg` and falls back to the
system TTS (Basque if the engine has it, else a Spanish voice) when a clip is not recorded yet
(`AndroidAudioProvider`, `core:data`); `slow = true` plays at 0.7×. `ExerciseClips` decides what
each exercise plays: the Basque prompt when it appears (listening, Basque prompts, meanings,
speaking) and the Basque answer after checking (translations into Basque, ordering, gaps, chats).
Story lines play as they appear. `SoundEffects` (`AndroidSoundEffects`, SoundPool) plays the
correct / incorrect / finished sounds, with haptic feedback (`HapticFeedbackType.Confirm` /
`Reject`); both can be turned off in the profile, as can listening exercises.

**Hints**: `CourseRepository.getGlossary` loads `glossary.json`; `Glossary.segment` (`core:model`)
splits a Basque text into runs, matching multi-word expressions first. `SessionPlan` carries the
glossary (none for legendary levels) and `LessonContent` provides it through `LocalGlossary`.
`LanguageText(hints = true)` turns hinted words into `LinkAnnotation`s with a dotted underline, so
TalkBack exposes them as links, and shows the meaning in a bubble (a polite live region) below.

**Stories** (`StoryRoute`, `StoryViewModel`, `feature:lesson`): the player shows the lines read so
far (tap a line for its translation, or its speaker button to hear it again) and the current
question. Finishing records the story level as completed.

## Learning path and progress

- **Progress** is stored in Room (`HitzDatabase`, `core:data`): `completions` (a lesson id, or the
  level id of a story, review or legendary level, with best first-try accuracy) and `mistakes` with
  `mistake_tags` (`lemma` / `structure`). Room schemas are exported to `core/data/schemas/` and
  committed by the screenshot-recording workflow.
- **Path rules** (`LearningPathBuilder`, pure Kotlin): progression is linear over the required
  levels (lessons, stories, unit and section reviews); a lesson level is complete when all its lessons are;
  legendary levels are optional and never block. Exactly one node is *current*.
- **Sessions** (`PlanSessionUseCase`): lesson levels play the next lesson; the **review** picks the
  12 exercises with the highest mistake score (3 × mistakes on the exercise + mistakes on its
  lemmas + mistakes on its structures) from its unit, or from every unit of the section for a
  **section review** (`scope: section`, closing each section), so spaced repetition follows the
  learner's weak words and grammar; the **legendary** level plays every unit exercise shuffled,
  allowing 3 mistakes.
  `RecordSessionUseCase` saves mistakes always and the completion only when the session is finished.
- `feature:path` shows sections, unit headers with the guidebook, and nodes whose Material
  expressive shape depends on the level type (lesson, story, review, legendary).
- **Unit introduction** (`UnitIntroRoute`, `feature:path`): the first time a learner opens a lesson
  of a unit, the path shows what the unit teaches first: its goals, its key words (`keyWords` in the
  unit JSON: the first core lemmas of the syllabus, with picture and audio `<unit>.kw<i>`) and the
  titles of its grammar notes, with a link to the guidebook. *Empezar* replaces it with the lesson.
  Seen introductions are stored in `UserSettings.seenUnitIntros` (DataStore).

## First run and placement

- **Introduction** (`:feature:onboarding`, `OnboardingRoute`): the start destination until
  `UserSettings.onboardingCompleted`. Five pages in a `HorizontalPager`: welcome, what the course
  covers (its sections), how it works (lessons, new words and hints, the daily goal, reviews, the
  score), the daily goal in minutes and "have you studied Basque before?". *Saltar* skips it as a
  beginner with the default goal. `MainActivity` keeps the splash screen until the setting is
  read, so the right start destination is chosen without a flash.
- **Placement tests** (`PlacementUseCase`, `core:domain`; `PlacementRoute`, played by the lesson
  screen): a learner who knows some Basque takes one test per section, in order. A test asks two
  exercises of each unit of the section, from its later lesson levels (no new word cards, no
  speaking), with no hints and no second chances. 80 % right skips the section: every
  required level of its units is recorded as completed (legendary levels stay optional), and the
  learner can try the next section or start learning. Placement tests do not count towards the daily goal.

## Accessibility and adaptive layout

- Every interactive element has a TalkBack label; path nodes, counters and options merge their
  semantics into one announcement; feedback is a polite live region; headings are marked.
- Text and the counters' icons scale with the font size (screenshots at 200 % for the component
  catalog, lesson, path and profile); long screens scroll instead of clipping.
- The instrumented tests (`MainActivityTest`) run the Accessibility Test Framework on every
  top-level screen and on a lesson (`enableAccessibilityChecks`): labels, touch targets, contrast.
- Decorative motion (the Lottie celebration, generated by `tools/design/make_celebration.py`) is
  skipped when the system "remove animations" setting is on (`rememberReducedMotion`).
- Windows 600 dp wide or more (tablets, foldables, landscape) use a Material 3 Expressive
  `WideNavigationRail` instead of the bottom `ShortNavigationBar`.

## Daily goal and achievements

Rules are pure Kotlin in `core:domain` (`eus.hitz.core.domain.gamification`); state lives in Room
(`activities`, `study_days`; schema version 3 via auto-migration, which drops the version 2
tables for XP, streaks, hearts, gems and leagues). Days follow the learner's local midnight
(`ZoneProvider`). There is no currency, no lives and no streak: nothing is lost on a missed day.

| Piece | Rules |
|---|---|
| Daily goal (`DailyGoalRules`) | Active minutes a day: 5, 10 (default), 15 or 20, chosen in the introduction and the profile. Each step of a lesson or story (answer, continue, tap) adds the time since the previous one, up to 1 minute, so idle time does not count. Unfinished sessions count too. Practice can go over 100 %. Changing the goal applies to today, not to past days. |
| Mistakes | Lessons and reviews allow any number of mistakes (they come back at the end). Legendary levels allow 3: the 4th ends the level, not completed. |
| Achievements (`Achievements`) | Days with the goal met (3/7/30/100, not necessarily in a row), study minutes, lessons, perfect sessions, stories; 4 tiers each, shown as badges in the profile. |

- `RecordSessionUseCase` saves mistakes, the completion and the study time
  (`RecordStudyTimeUseCase`); `CompleteSessionUseCase` also records the activity and returns a
  `SessionReward` (daily goal before and after, new achievement tiers). The end screens show it;
  the session that reaches 100 % gets the celebration ("you've done what you need for today").
- `ObservePlayerUseCase` gives the `PlayerSummary`: today, the week (Monday first), days with the
  goal met, total minutes and achievements. The path shows today's ring; it opens the profile.
- **Daily goal reminder**: opt-in from the profile (asks for the notification permission on
  Android 13+). `WorkManagerReminderScheduler` runs `DailyGoalReminderWorker` daily around 19:00;
  it notifies only if today's goal is not met yet ("X minutes left"). It replaces the old streak
  reminder, which is cancelled.

## Score

The profile shows a score on a CEFR-aligned scale: 0 to 29 covers A1, so finishing the course
gives 29 (`ScoreRules`, `ObserveScoreUseCase`, `eus.hitz.core.domain.score`).

- Every *required* step counts the same: each lesson of a lesson level, each story and each review
  (legendary levels are optional practice). Score = ⌊29 × steps done / steps⌋; the bar shows the
  way to the next point.
- Bands: 0–9 first steps, 10–19 solid basics, 20–29 A1 within reach, each with a short
  description.
- The history is rebuilt from the first completion time of each step
  (`ProgressRepository.observeCompletions`), so it needs no storage of its own and includes
  sections skipped with a placement test. The score page (`ScoreRoute`) draws it as a line, with
  the points gained this week.
- "What you can already do" lists the goals of every finished unit.

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
