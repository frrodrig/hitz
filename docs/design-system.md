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

- **Mascot:** a *Galtzagorri*, one of the tiny red-trousered helpers of Basque mythology, drawn
  as a flat, geometric character: a one-piece capsule body (face above, the red *galtzak* below),
  thin stroke limbs, a small *txapela*, a bushy beard with sideburns, wild eyebrows, a *makila*
  (walking stick) with a red tassel and *abarkak*. Four poses (`MascotPose`: hola, bien, ups,
  piensa) are generated from one SVG template (`tools/design/art/galtzagorri.svg.template`) by
  `tools/design/mascot_to_vector.py`, which writes the vector drawables and the reviewed SVGs in
  `docs/art/`. The script also splits each pose into layers (`data-layer` in the template: shadow,
  sparkles, back, arm, body, eyes, front, thought) that share the canvas, and `HitzMascot` animates
  them with `graphicsLayer` around real pivots (feet, shoulders, eyes): he breathes and blinks in
  every pose; *Hola* waves every 5 s; *Bien* jumps when he appears and wiggles both arms while the
  sparkles twinkle; *Ups* shakes his head once; *Piensa* sways while his thought bubbles float.
  Values are read in the draw phase only (no recomposition per frame), every animation starts at
  rest (screenshots show the static pose), and with reduced motion or `animated = false` the whole
  drawable is shown instead.
- **App icon:** a *lauburu* of four arms (red, blue, ochre, sea green) on cream. Adaptive icon with
  vector background, foreground and monochrome (themed icon, Android 13+) layers of 108 dp; the
  arms reach a 32 dp radius, inside the 66 dp safe zone, and each one is shrunk around its own
  centre to leave a gap between them. `docs/store/icon-512.png` is the Google Play icon, rendered
  from the same geometry. The splash screen uses the cream background and the foreground layer.
- **Brand seeds:** itsaso blue `#385F97` (primary), Cantabrian-sea teal `#1F6F78` (secondary)
  and ochre `#B7791F` (tertiary), with warm cream neutrals. These are three of the four colours
  of the lauburu icon; the fourth, red `#D7263D`, is kept for illustration (icon, mascot) and is
  never a UI role, so in the interface red only ever means an error.

## Colour

`theme/Color.kt` is **generated** by `tools/design/generate_color_scheme.py` with the Material
Color Utilities algorithm (`pip install materialyoucolor`, tonal-spot variant). It produces:

| Scheme | Used when |
|---|---|
| `LightColorScheme` / `DarkColorScheme` | Default |
| `LightHighContrastColorScheme` / `DarkHighContrastColorScheme` | System contrast ≥ 0.5 (Android 14+) or `highContrast = true` |
| Dynamic colour | Opt-in setting (`dynamicColor = true`, Android 12+) |

Extended colour families (`ColorFamily`: `color`, `onColor`, `container`, `onContainer`):

| Family | Use |
|---|---|
| `success` | Correct answers, lesson progress, daily goal met |
| `warning` | "Almost correct" feedback |

**Feedback families.** After *Check*, `HitzFeedbackPanel` takes the result's family for the sheet
(`container`), the icon badge and the button (`color`): green when right, ochre when almost, error
red when wrong. The primary blue is only used before checking.

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

## Expressive tactics applied

| Tactic | Where |
|---|---|
| **Containment and grouping** | Answer options, practice units, achievements and settings form groups (2 dp gaps, large outer and small inner corners); the daily goal and the score sit in tonal chips; the daily-goal choice is a connected button group (`ToggleButton` + `ButtonGroupDefaults.connected*ButtonShapes`), which replaces segmented buttons. |
| **Shape morphing** | Buttons morph on press (`ButtonDefaults.shapes()`); the active option morphs to fully rounded corners. |
| **Shape variety** | `MaterialShapes` containers for the framed mascot (`Sunny`), the feedback badge (`Cookie9Sided` / `Cookie4Sided`), path nodes (one shape per level type) and unit badges. |
| **Motion of the current step** | The current path node is 96 dp and its shape turns slowly (16 s a turn), like the expressive loading indicator; the icon stays upright. |
| **Sticky context** | On the path, the unit header stays on top while its levels scroll, coloured by unit (primary, secondary, tertiary in turn) with its progress as a wavy bar. |
| **Motion** | All state animations use `MaterialTheme.motionScheme` springs (spatial for shape, effects for colour). |
| **Emphasized type** | Screen and question titles, feedback result and counter values. |
| **Tonal colour** | Tonal containers carry state; bold colour is reserved for the primary action and the hero of each screen (current unit, personal review). The profile's score card sits on `primaryContainer`; cards are tonal (`surfaceContainer`), not outlined. |
| **Lessons** | The prompt is said by the Galtzagorri in a speech bubble (its small corner points at the mascot; with fonts over 150 % the mascot steps aside). The end of a lesson is a hero on `tertiaryContainer` with the mascot, then three result tiles (first-try accuracy, active time, today's goal on the success container when met), each with one small corner towards the next. |
| **Navigation** | Detail screens (guide, unit introduction, score, unit review) go back with a Back arrow; only full-screen sessions (lessons, stories) close with an X. |

## Icons

`HitzIcons`: original icons on a 24 dp grid. UI icons use 2 dp rounded strokes; counters
(bolt, trophy, star) are solid shapes. All are tinted by the caller.

## Illustrations

Course pictures (165, for "choose the image") share one flat style: a rounded square with a soft
background per family (people, places, food…), big simple shapes with no outlines, a darker shade
for depth and no text. The palette reuses the theme's red, teal and navy plus natural colours, and
people show a range of skin and hair colours. They are drawn as code (`tools/design/illustrations/`),
so they stay consistent and any change is a reviewable diff; see the content guide for the workflow.

## Components

| Hitz component | Built on | Purpose |
|---|---|---|
| `HitzButton` | `Button`, `FilledTonalButton`, `OutlinedButton`, `TextButton` with `ButtonDefaults.shapes()` (shape morph) and expressive sizes | Actions |
| `HitzChoiceCard` / `HitzChoiceGroup` | `ListItem` + `RadioButton` on tonal surfaces, grouped as a segmented list | Answer option: default, selected, correct, incorrect, disabled |
| `HitzWordChip` / `HitzWordBank` | 48 dp `Button` tile (lightest container, 2 dp outline, 16 dp corners) in a `FlowRow` | Word bank; used words keep an empty slot. In lessons the answer is built in a tonal tray ruled under each row of tiles |
| `HitzFeedbackPanel` | `Surface` + `ListItem` + `MaterialShapes` badge + `HitzButton` | Result panel, announced as a live region |
| `HitzLessonProgressBar` | `LinearWavyProgressIndicator` | Lesson progress |
| `HitzDailyGoalRing` / `HitzDailyGoalChip` | `CircularWavyProgressIndicator` (flat with a check mark when met), in a tonal container | Today's share of the daily goal, spoken as a percentage |
| `HitzMascot` | Animated stack of a `MascotPose`'s layers (or its single `Image` with reduced motion), optionally framed on a `MaterialShapes.Sunny` container | The Galtzagorri: intro, lesson end, errors, avatar |
| `PlaceholderScreen` | — | Temporary screen for unfinished features |

The app uses `ShortNavigationBar` (or `WideNavigationRail` on wide windows) for its three top-level
destinations, Aprender, Practicar and Perfil, with a filled icon for the selected one. Every component has `@HitzPreviews`
(light, dark, 200 % font and Basque). `ComponentCatalog` shows them all and is captured by
`ComponentCatalogScreenshotTest` (light, dark, high contrast, 200 % font).
