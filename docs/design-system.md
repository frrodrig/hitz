# Design system

Everything lives in `:core:designsystem`. Features never hard-code colours, fonts, sizes or icons:
they use `MaterialTheme`, `HitzTheme.extendedColors`, `HitzSpacing` and `HitzIcons`.

The UI follows **Material 3 Expressive**. `HitzTheme` wraps `MaterialExpressiveTheme` with the
expressive motion scheme, and components are standard Material 3 / Expressive components wherever
one exists; Hitz wrappers only fix sizes, colours and accessibility semantics.

> Expressive APIs are public from `androidx.compose.material3:material3` **1.5.0** (the 1.4.x
> version in the Compose BOM keeps them internal), so the catalog pins material3 to the latest
> 1.5.0 pre-release. Move back to the BOM version once 1.5.0 is stable.

## Identity

- **Mascot:** a *Galtzagorri*, one of the tiny red-trousered helpers of Basque mythology, wearing a
  *txapela* (beret). Drawn in-house as a vector (`core_designsystem_mascot`); the launcher icon is
  its head on the brand red.
- **Brand seeds:** Galtzagorri red `#C8102E` (primary), Cantabrian-sea teal `#1F6F78`
  (secondary) and ochre `#B7791F` (tertiary), with warm neutrals.

## Colour

`theme/Color.kt` is **generated** by `tools/design/generate_color_scheme.py` with the Material
Color Utilities algorithm (`pip install materialyoucolor`). It produces:

| Scheme | Used when |
|---|---|
| `LightColorScheme` / `DarkColorScheme` | Default |
| `LightHighContrastColorScheme` / `DarkHighContrastColorScheme` | System contrast ≥ 0.5 (Android 14+) or `highContrast = true` |
| Dynamic colour | Opt-in setting (`dynamicColor = true`, Android 12+) |

Extended colour families (`ColorFamily`: `color`, `onColor`, `container`, `onContainer`):

| Family | Use |
|---|---|
| `success` | Correct answers, lesson progress |
| `xp` | XP and "almost correct" feedback |
| `streak` | Streak counter |
| `gem` | Gems |
| `heart` | Hearts |

`ColorContrastTest` checks that every text/background pair in every scheme meets WCAG AA (4.5:1).
Never use colour alone to convey meaning: correct and incorrect also show an icon and a spoken state.

## Typography

**Google Sans Flex** (the Material 3 Expressive typeface, SIL OFL 1.1, `third_party/fonts/`) for every
role, including the emphasized styles (`titleLargeEmphasized`, …). The font is subset to Latin and
instanced to its weight axis (≈180 KB). Sizes follow the Material 3 scale in `sp`, so they respect
the user's font scale; screenshots are also taken at 200 %.

## Shapes and spacing

Default Material 3 Expressive `Shapes()`, plus `MaterialShapes` (e.g. `Cookie9Sided`, `Sunny`) for
decorative containers. Spacing: 4 dp grid (`HitzSpacing`), with 48 dp minimum touch targets.

## Icons

`HitzIcons`: original icons on a 24 dp grid. UI icons use 2 dp rounded strokes; counters
(streak, gem, heart, XP, star) are solid shapes. All are tinted by the caller.

## Components

| Hitz component | Built on | Purpose |
|---|---|---|
| `HitzButton` | `Button`, `FilledTonalButton`, `OutlinedButton`, `TextButton` with `ButtonDefaults.shapes()` (shape morph) and expressive sizes | Actions |
| `HitzChoiceCard` | `ListItem` + `RadioButton` on a tonal `Surface` | Answer option: default, selected, correct, incorrect, disabled |
| `HitzWordChip` / `HitzWordBank` | `FilledTonalButton` in a `FlowRow` | Word bank; used words keep an empty slot |
| `HitzFeedbackPanel` | `Surface` + `ListItem` + `MaterialShapes` badge + `HitzButton` | Result panel, announced as a live region |
| `HitzLessonProgressBar` | `LinearWavyProgressIndicator` | Lesson progress |
| `HitzStatCounter` | `AssistChip` | Streak, gems, hearts and XP with spoken plurals |
| `HitzMascot` | Image on a `MaterialShapes.Sunny` container | The Galtzagorri |
| `PlaceholderScreen` | — | Temporary screen for unfinished features |

The app uses `ShortNavigationBar` for top-level navigation. Every component has `@HitzPreviews`
(light, dark, 200 % font and Basque). `ComponentCatalog` shows them all and is captured by
`ComponentCatalogScreenshotTest` (light, dark, high contrast, 200 % font).
