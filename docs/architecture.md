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
:core:data                 Repositories, Room, DataStore, content assets
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
- **Use cases** in `core:domain` (`operator fun invoke`), injected with Hilt.
- **Offline-first**: the source of truth is local (Room and DataStore). `SyncRepository` is the
  seam for a future backend; today `OfflineSyncRepository` returns `SyncResult.Disabled`.
- **Type-safe navigation** with `@Serializable` routes defined by each feature
  (`feature/*/navigation`).

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
- Project dependencies use `project(":…")`: type-safe project accessors break on a module
  named `path` (it clashes with `ProjectDependency.getPath()`).
- Style: **detekt** with `detekt-formatting` (ktlint rules) runs once from the root project over
  all sources, so it does not depend on the variant API removed in AGP 9.

## CI/CD (GitHub Actions, free tier)

| Workflow | Trigger | What it does |
|---|---|---|
| `ci.yml` | PRs and pushes to `main` | Syllabus check, detekt, Lint, unit tests, debug build, APK artifact |
| `ui-tests.yml` | Manual and nightly | Roborazzi (verify, or record and open a PR) and instrumented tests on an emulator |
| `release.yml` | `v*` tag | Signed AAB and APK, GitHub Release with generated changelog |
| `pr-title.yml` | PRs | Conventional Commits title |

Gradle failures are re-published as check annotations by `.github/scripts/gradlew-annotated.sh`,
so they are readable from the PR checks tab without downloading logs. Every workflow uses
`concurrency` with cancellation and the Gradle cache (written only on `main`). The repository is
public, so standard runner minutes are unlimited.
