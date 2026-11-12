package eus.hitz.feature.practice

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import eus.hitz.core.model.content.LocalizedText

/** The text in the UI language: Basque when the app runs in Basque, Spanish otherwise. */
@Composable
internal fun LocalizedText.resolve(): String =
    if (LocalConfiguration.current.locales[0].language == "eu") eu else es
