package eus.hitz.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private const val DARK_BACKGROUND = 0.5f

/**
 * The surface for what is read or pressed above the page: the sentence of an exercise, word keys,
 * the card that starts the current step. In light themes it is the lightest container (white); in
 * dark themes the lowest container is the darkest one, so it is a high container instead, a step
 * lighter than the page. Works for the brand schemes and dynamic colour alike.
 */
val ColorScheme.raisedSurface: Color
    get() = if (background.luminance() < DARK_BACKGROUND) surfaceContainerHigh else surfaceContainerLowest
