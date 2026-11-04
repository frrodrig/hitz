# Design system

Everything lives in `:core:designsystem`. Features never hard-code colours, fonts, sizes or icons:
they use `MaterialTheme`, `HitzTheme.extendedColors`, `HitzSpacing` and `HitzIcons`.

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

| Role | Family |
|---|---|
| Display, headline, title, labelLarge (buttons) | **Fredoka** (variable, rounded) |
| Body, labelMedium, labelSmall | **Atkinson Hyperlegible** (designed for low-vision legibility) |

Both fonts are SIL Open Font License 1.1 (`third_party/fonts/`). Sizes follow the Material 3 scale
in `sp`, so they respect the user's font scale; screenshots are also taken at 200 %.

## Shapes and spacing

Corner radii: 6 / 10 / 16 / 24 / 32 dp (`HitzShapes`). Spacing: 4 dp grid (`HitzSpacing`), with
48 dp minimum touch targets.

## Icons

`HitzIcons`: original icons on a 24 dp grid. UI icons use 2 dp rounded strokes; counters
(streak, gem, heart, XP, star) are solid shapes. All are tinted by the caller.

## Components

| Component | Purpose |
|---|---|
| `HitzButton` | M3 buttons (primary, tonal, outlined, text) at 52 dp with optional icon |
| `HitzChoiceCard` | Answer option with default, selected, correct, incorrect and disabled states |
| `HitzWordChip` / `HitzWordBank` | Word-bank chips that keep their slot when used |
| `HitzFeedbackPanel` | Result panel (correct, almost, incorrect) announced as a live region |
| `HitzLessonProgressBar` | Animated lesson progress |
| `HitzStatCounter` | Streak, gems, hearts and XP counters with spoken plurals |
| `HitzMascot` | The Galtzagorri |
| `PlaceholderScreen` | Temporary screen for unfinished features |

Every component has `@HitzPreviews` (light, dark, 200 % font and Basque). `ComponentCatalog`
shows them all and is captured by `ComponentCatalogScreenshotTest` (light, dark, high contrast,
200 % font).
