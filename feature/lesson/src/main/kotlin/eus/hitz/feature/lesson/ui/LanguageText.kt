package eus.hitz.feature.lesson.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.LocaleList
import eus.hitz.core.model.content.Language

internal val Language.localeList: LocaleList
    get() = when (this) {
        Language.Basque -> LocaleList("eu")
        Language.Spanish -> LocaleList("es")
    }

/**
 * Text tagged with its language, so TalkBack and hyphenation use the right locale even when the
 * UI is in the other language.
 */
@Composable
internal fun LanguageText(
    text: String,
    language: Language,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    Text(text = text, modifier = modifier, style = style.copy(localeList = language.localeList))
}
