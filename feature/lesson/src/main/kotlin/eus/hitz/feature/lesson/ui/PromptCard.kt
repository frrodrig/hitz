package eus.hitz.feature.lesson.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.R

/** The sentence to work on, on a tonal container, with an optional button to hear it. */
@Composable
internal fun PromptCard(
    text: String,
    language: Language,
    modifier: Modifier = Modifier,
    onPlay: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(Modifier.padding(HitzSpacing.lg), verticalAlignment = Alignment.CenterVertically) {
            LanguageText(
                text = text,
                language = language,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
            )
            if (onPlay != null) {
                IconButton(onClick = onPlay) {
                    Icon(
                        painter = painterResource(HitzIcons.Speaker),
                        contentDescription = stringResource(R.string.feature_lesson_listen),
                    )
                }
            }
        }
    }
}

@HitzPreviews
@Composable
private fun PromptCardPreview() {
    HitzTheme { PromptCard("Kaixo! Ni Ane naiz.", Language.Basque, Modifier.padding(HitzSpacing.lg), onPlay = {}) }
}
