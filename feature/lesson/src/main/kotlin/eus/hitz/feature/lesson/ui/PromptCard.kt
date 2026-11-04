package eus.hitz.feature.lesson.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language

/** The sentence to work on, on a tonal container. */
@Composable
internal fun PromptCard(
    text: String,
    language: Language,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        LanguageText(
            text = text,
            language = language,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(HitzSpacing.lg),
        )
    }
}

@HitzPreviews
@Composable
private fun PromptCardPreview() {
    HitzTheme { PromptCard("Kaixo! Ni Ane naiz.", Language.Basque, Modifier.padding(HitzSpacing.lg)) }
}
