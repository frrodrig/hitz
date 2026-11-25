# Redesign B: "each unit, a place"

The direction chosen in November 2026 after a visual audit of the app (boards: the *Hitz · Rediseño*
design canvas). The app followed Material 3, but nothing led the eye: the type scale was flat,
surfaces were all the same cream, the largest thing on the path was a locked challenge and almost
nothing moved. Direction B keeps the structure (vertical path of nodes, lesson layout, three
destinations) and gives it a clear hierarchy and a personality built from what is already Hitz's
own: the cast, their scenes and Google Sans Flex.

## Principles

1. **One thing to look at per screen.** Bold colour (primary) is for the next action only. The
   rest is tonal or neutral.
2. **The Basque sentence is the hero of an exercise**: large (`lessonPhrase`), on the lightest
   surface, with the cast speaking it.
3. **Units are places.** Each unit opens with a cover: its scene, its host and a wide title.
4. **Read on white, group on tone.** Text to read sits on `surfaceContainerLowest`; groups sit on
   tonal containers; the background stays cream. Fewer containers overall.
5. **Two hero moments only**: the start of a unit and the end of a lesson, revealed in sequence.
6. **Motion explains state** (see [Motion](../design-system.md#motion)).
7. **Colour keeps one meaning each**: primary = act, secondary = path walked, tertiary (ochre) =
   achievement, error red = wrong. Almost-right answers get their own icon.

## Screens

| Screen | Change |
|---|---|
| Path | One title (the section) and labelled chips; each unit opens with a cover (scene, host, `displaySmall` title, segmented progress, guide); a continuous track joins the nodes, walked part in secondary; size follows priority (current 96 dp with an action card, locked 52 dp); the star challenge is a node at the end of its unit, its scene moves to the cover. On wide windows, a supporting pane. |
| Lesson | Instruction as a small label; the sentence in `lessonPhrase` on a white card with the speaker beside it; word tiles as keys (raised base), word bank docked above the button; feedback sheet rises with a spring; "almost" has its own icon and a "Solution" line. |
| Lesson end | Hero: the host celebrating and first-try accuracy as a `heroNumeral`; results as a connected group; the achievement as a card with its badge. |
| Practice, review | Word strength in shades of one colour, never error red. |

## Delivery

One PR, `feat/redesign-b`, one commit per part, its screenshots recorded by the UI tests workflow:

1. **Foundations**: type scale with the width axis, motion scheme and navigation transitions.
2. **Path**: cover, track, nodes, top bar, challenge node; unlock and scroll motion.
3. **Lesson**: phrase card, key tiles, docked word bank, feedback sheet; tile and sheet motion.
4. **Lesson end and fixes from the first screenshots**: hero reveal, count-up, connected results;
   the raised surface in dark themes; the top bar at large font sizes; word strength without the
   error colour.

Still to do: the supporting pane on wide windows (the path is still the phone layout stretched).
