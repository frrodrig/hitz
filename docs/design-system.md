# Design system

Everything lives in `:core:designsystem`. Features never hard-code colours, fonts, sizes or icons:
they use `MaterialTheme`, `HitzTheme.extendedColors`, `HitzSpacing` and `HitzIcons`.

The UI follows **Material 3 Expressive**. `HitzTheme` wraps `MaterialExpressiveTheme` with the
expressive motion scheme, and components are standard Material 3 / Expressive components wherever
one exists; Hitz wrappers only fix sizes, colours and accessibility semantics.

> Expressive APIs are public from `androidx.compose.material3:material3` **1.5.0** (the 1.4.x
> versions keep them internal), so the catalog pins material3 to the stable 1.5.0. The project
> only uses stable library versions.

## Identity

- **Cast:** instead of one mascot, four Basque friends talk to the learner, one per arm of the
  lauburu in the icon (`CastMember`). Each has a colour and a role, so the same person always
  appears in the same kind of moment (`CastMember.speakerFor` picks them from the pose):

  | Member | Colour | Who | Appears in |
  |---|---|---|---|
  | **Ane** | red | young *trikitilari* with a ponytail, a white *zapia* and a trikitixa | lesson prompts, unit introductions, onboarding |
  | **Mikel** | blue | *aizkolari* with a beard, a *txapela* and an axe on his shoulder | the end of a lesson, stories finished, placement passed |
  | **Maite** | ochre | *amona* with a bun, round glasses and a book | mistakes, errors, hints |
  | **Unai** | sea green | teenage *pelotari* with a headband, a pelota and a txistera | practice and, later, leagues and quests |

  They share one construction (round head over a capsule body, flat shapes with white cut-out
  lines and a little volume, 240 × 280 canvas) and four poses (`MascotPose`): *Hola* is each one's own loop (Ane
  plays the trikitixa, Mikel waves, Maite turns a page, Unai bounces the pelota); *Bien* jumps
  with an arm up among sparkles; *Ups* puts a hand to the head, shakes it and sweats; *Piensa*
  holds the chin, sways and thinks in bubbles. Everyone keeps their own object and gestures with
  the free hand, with a white outline so a sleeve reads over a shirt of the same colour.
- **Flat with depth:** every large shape has a shade (the shape minus a copy moved up and left,
  in its colour mixed with a warm dark brown) and a faint rim light, as if lit from the upper
  left. `tools/design/add_depth.py` adds them to the drawings as clip groups after each shape and
  rebuilds them on every run, so the flat shapes stay the ones to edit (`data-depth="none"` opts
  a shape out, `data-depth="dx dy"` sets its offset); its `--check` runs in the tools tests.
- **Star challenge scenes:** each unit's challenge shows a scene of its theme in a circle
  (`HitzScene`): a place, the unit's host and a prop with its own loop (steam from a pot, a
  bottle into the bin, a pelota on the frontón…). They are SVGs in
  `tools/design/art/scenes/<unit>.svg`, 200 × 200, with the same motions; `<g data-host="x y
  height" />` marks where the host stands, so shapes before it are drawn behind them and the rest
  in front. Locked, the scene is grey and still.
- **Cast art pipeline:** the 16 drawings are SVGs in `tools/design/art/cast/<member>-<pose>.svg`.
  A group with `data-motion` moves on its own: the value names a `Motion` in `CastMotion.kt`
  (keyframes for translation, rotation, scale and alpha, a period and a delay) and `data-pivot`
  is the point it turns around. `tools/design/cast_to_vector.py` writes one vector for the whole
  drawing (shown with reduced motion or `animated = false`), one vector per layer (still shapes
  merged, each animated group its own layer) and `CastArt.kt`, the layer tree with motions and
  pivots; it does the same for the scenes (`SceneArt.kt`); `--check` (run by the tools tests in
  CI) fails when they are out of date.
  `HitzMascot` stacks the layers and moves each with `graphicsLayer` from one frame clock read in
  the draw phase, so nothing recomposes per frame and a paused clock draws the pose at rest. The
  design canvas plays the same keyframes as CSS.
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

**The interface follows the colours the learner chose for Android** (Material You, dynamic colour,
Android 12+), unless they turn *Usar los colores del sistema* off in Ajustes. Hitz's identity does
not depend on the interface colours: it lives in the icon, the cast, the scenes and the
celebrations, which keep their own colours with any scheme. The answer families (success,
warning) are fixed too, so right, almost and wrong always look the same.

Hitz's own scheme (below Android 12, or with the setting off) is in `theme/Color.kt`, **generated**
by `tools/design/generate_color_scheme.py` with the Material Color Utilities algorithm
(`pip install materialyoucolor`, tonal-spot variant). Primary and tertiary keep their seeds' own
chroma and the teal most of it (26), so the brand stays recognisable without loud containers. It
produces:

| Scheme | Used when |
|---|---|
| `LightColorScheme` / `DarkColorScheme` | Below Android 12, or with the system's colours turned off |
| `LightHighContrastColorScheme` / `DarkHighContrastColorScheme` | System contrast ≥ 0.5 (Android 14+) or `highContrast = true` |
| Dynamic colour | Default on Android 12+ (`UserSettings.systemColors`, passed as `dynamicColor`); Ajustes turns it off |

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
role. The font is subset to Latin and keeps two of its axes, weight (100–900) and width (100–125),
≈240 KB; `tools/design/subset_font.py` rebuilds it from the variable font in google/fonts. Optical
size is pinned at its default: keeping it would add ≈400 KB.

The scale keeps the Material 3 sizes and line heights (in `sp`, so it follows the user's font
size; screenshots are also taken at 200 %) and makes the levels distinct with weight and width:

| Role | Weight | Width | Emphasized |
|---|---|---|---|
| Display | Bold (700) | Wide (112) | ExtraBold (800), widest (125) |
| Headline | SemiBold (600) | Normal | Bold (700), wide (112) |
| Title | SemiBold (600) | Normal | Bold (700) |
| Body | Regular (400) | Normal | SemiBold (600) |
| Label | Medium (500) | Normal | Bold (700) |

Width is only for short text that carries a screen (unit titles, results, screen titles); body
text stays at normal width so long Basque and Spanish sentences read easily. Two styles break the
scale on purpose (`HitzTextStyles`):

- `heroNumeral`: 88 sp, ExtraBold, widest. The figure of a hero moment (the end of a lesson).
- `lessonPhrase`: 30 sp, SemiBold, normal width. The sentence an exercise is about.

## Shapes and spacing

Default Material 3 Expressive `Shapes()`, plus `MaterialShapes` (e.g. `Cookie9Sided`, `Sunny`) for
decorative containers. Spacing: 4 dp grid (`HitzSpacing`), with 48 dp minimum touch targets.

## Raised surface

What is read or pressed above the page (the sentence of an exercise, word keys, the card that starts
the current step, the achievement card) sits on `colorScheme.raisedSurface`: the lowest container
(white) in light themes and a high container in dark ones, where the lowest container is the darkest
and would read as a hole in the page.

## Motion

Motion follows the Material 3 Expressive physics system: springs, not durations
(`HitzMotionScheme` = `MotionScheme.expressive()`, set on the theme). Read the tokens from
`MaterialTheme.motionScheme`:

| Token | For | Examples |
|---|---|---|
| `fastSpatialSpec` | Small things that move, grow or change shape | Word tiles, buttons, chips, the current node's press |
| `defaultSpatialSpec` | Parts of the screen | The feedback sheet, a node unlocking, detail screens sliding in |
| `slowSpatialSpec` | Whole screens and hero moments | A lesson rising over the path, the end-of-lesson reveal |
| `*EffectsSpec` | Colour and opacity, never overshooting | State colours, fades, progress |

Rules:

- **Spatial springs move, effects springs fade.** Never animate opacity or colour with a spatial
  spring (it would overshoot), and use effects springs for sizes that can shrink to zero.
- **Every destination kind has its axis** (`HitzTransitions`, applied in `HitzApp`): top-level
  destinations fade through; detail screens move forward and back along the horizontal axis
  (predictive back scrubs it); sessions (lessons, stories, placement) rise from the bottom over the
  path and sink back. Full-screen slides use the standard scheme so no gap opens at the far edge.
  The navigation bar and rail make way for full-screen destinations.
- **Motion explains a change of state**: something appears where it comes from, a result arrives
  from below, an unlocked node grows into place. Decoration loops only on the current step and the
  cast.
- **Hero moments reveal in sequence** (`HitzReveal`, 70 ms apart, slow spatial lift + fade): only at
  the start of a unit and the end of a lesson.
- **Reduced motion**: with *Remove animations* on (`rememberReducedMotion()`), reveals, loops and
  the cast's movement are skipped and content is simply there; previews show the end state.

## Expressive tactics applied

| Tactic | Where |
|---|---|
| **Containment and grouping** | Answer options, practice units, achievements and settings form groups (2 dp gaps, large outer and small inner corners); the daily goal and the score sit in tonal chips; the daily-goal choice is a connected button group (`ToggleButton` + `ButtonGroupDefaults.connected*ButtonShapes`), which replaces segmented buttons. Settings have their own screen, **Ajustes**, behind the gear in the profile's header, grouped by what they change: exercises, sound and reminders, appearance (system colours, Android 12+), language (Android's per-app language, Android 13+) and the app (update checks, version, typeface licence). The profile keeps who the learner is and how they are doing, and the daily-goal choice next to the goal. |
| **Shape morphing** | Buttons morph on press (`ButtonDefaults.shapes()`); the active option morphs to fully rounded corners. |
| **Shape variety** | `MaterialShapes` containers for the framed mascot (`Sunny`), the feedback badge (`Cookie9Sided` / `Cookie4Sided`) and the current path step (`Cookie9Sided`); every other step is a circle, so the current one stands out. |
| **Motion of the current step** | The current path node is 96 dp and its shape turns slowly (16 s a turn), like the expressive loading indicator; the icon stays upright. |
| **Units as places** | On the path each unit opens with a cover on `secondaryContainer` (neutral and grey while locked): its scene and host, a display title, its first goal, the guide and its levels as segments. Once the cover scrolls away, a compact unit bar slides down and stays on top. One title names the section; the score chip says "pts". |
| **The path** | A continuous 8 dp track joins the steps, walked in `secondary`. Size follows priority: the current step is 96 dp, the one expressive shape on the path, with a card under it that starts it; walked steps are 56 dp circles with a check, locked ones 52 dp with a lock, titles beside them. The star challenge is the last step of its unit, in the achievement colour. On wide windows the path stays 600 dp wide and, from 840 dp, the current unit gets a pane beside it: its cover, what it teaches and a Continue button. |
| **Unlocking** | Back from a lesson, the track walks on to the new step and the step pops into place (slow effects fill, default spatial pop); the section name slides when it changes. |
| **Motion** | All state animations use `MaterialTheme.motionScheme` springs (spatial for shape, effects for colour); see [Motion](#motion). |
| **Emphasized type** | Screen and question titles, feedback result and counter values, set bold and wide (see [Typography](#typography)). |
| **Tonal colour** | Tonal containers carry state; bold colour is reserved for the primary action and the hero of each screen (current unit, personal review). The profile's score card sits on `primaryContainer`; cards are tonal (`surfaceContainer`), not outlined. |
| **Lessons** | The instruction is a small label in primary; the sentence is the hero, said by Ane in a white speech bubble in `lessonPhrase` (its small corner points at her; with fonts over 150 % she steps aside). The answer is built on ruled lines with no container, and the word bank is docked above Check, where the thumb is. Each exercise moves in from the right; the feedback sheet rises with a spatial spring. A wrong answer says "No es correcto" and then "Solución: …"; an almost-right one has its own pencil icon. Mistakes are met by Maite. The end of a lesson is a hero moment on `tertiaryContainer`: Mikel celebrating and first-try accuracy as a `heroNumeral` that counts up, revealed in sequence; then active time and today's goal as a connected pair (a met goal reads "Cumplido" on the success container, never "105 %"), and a new achievement as a card with its badge. |
| **Navigation** | Detail screens (guide, unit introduction, score, unit review) go back with a Back arrow; only full-screen sessions (lessons, stories) close with an X. |

## Icons

`HitzIcons`: original icons on a 24 dp grid. UI icons use 2 dp rounded strokes; counters
(bolt, trophy, star, level) are solid shapes. All are tinted by the caller. The star belongs to
the star challenges (`HitzStars`: earned stars in `tertiary`, the rest in `outlineVariant`); the
score uses the level icon, three rising bars.

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
| `HitzWordChip` / `HitzWordBank` | 48 dp `Button` key (lightest container, 1 dp outline, 14 dp corners) on a 3 dp base, in a `FlowRow` | Word bank; keys sink onto their base when pressed and spring in when they appear; used words leave a dashed slot. In lessons the answer is built on lines ruled under each row of tiles |
| `HitzFeedbackPanel` | `Surface` + `ListItem` + `MaterialShapes` badge + `HitzButton` | Result panel: check, pencil (almost) or cross; announced as a live region |
| `HitzLessonProgressBar` | `LinearWavyProgressIndicator` | Lesson progress: the only wavy indicator, because it is the only progress under way |
| `HitzDailyGoalRing` / `HitzDailyGoalChip` | `CircularProgressIndicator` (with a check mark when met), in a tonal container | Today's share of the daily goal, spoken as a percentage |
| `HitzMascot` | Animated stack of a `CastMember`'s layers in a `MascotPose` (or its single `Image` with reduced motion), optionally framed on a `MaterialShapes.Sunny` container | The cast: prompts, lesson end, errors, empty states, avatar |
| `PlaceholderScreen` | — | Temporary screen for unfinished features |

The app uses `ShortNavigationBar` (or `WideNavigationRail` on wide windows) for its three top-level
destinations, Aprender, Practicar and Perfil, with a filled icon for the selected one. Every component has `@HitzPreviews`
(light, dark, 200 % font and Basque). `ComponentCatalog` shows them all and is captured by
`ComponentCatalogScreenshotTest` (light, dark, high contrast, 200 % font).
