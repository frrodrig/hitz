package eus.hitz.feature.lesson.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzButtonSize
import eus.hitz.core.designsystem.component.HitzButtonStyle
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.component.MascotPose
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.R

private val MASCOT_SIZE = 96.dp
private val BUBBLE_CORNER = 24.dp
private val BUBBLE_TAIL = 8.dp

/** With large fonts the sentence needs the whole width, so the mascot steps aside. */
private const val MASCOT_MAX_FONT_SCALE = 1.5f

/**
 * The sentence to work on, said by the Galtzagorri in a speech bubble, with an optional button to
 * hear it. The bubble's small corner points at the mascot.
 */
@Composable
internal fun PromptCard(
    text: String,
    language: Language,
    modifier: Modifier = Modifier,
    onPlay: (() -> Unit)? = null,
) {
    val showMascot = LocalDensity.current.fontScale < MASCOT_MAX_FONT_SCALE
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
    ) {
        if (showMascot) HitzMascot(pose = MascotPose.Hola, size = MASCOT_SIZE, contentDescription = null)
        Surface(
            modifier = Modifier.weight(1f).padding(top = if (showMascot) HitzSpacing.sm else 0.dp),
            shape = RoundedCornerShape(
                topStart = BUBBLE_CORNER,
                topEnd = BUBBLE_CORNER,
                bottomEnd = BUBBLE_CORNER,
                bottomStart = if (showMascot) BUBBLE_TAIL else BUBBLE_CORNER,
            ),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Column(
                modifier = Modifier.padding(
                    start = HitzSpacing.lg,
                    end = HitzSpacing.lg,
                    top = HitzSpacing.lg,
                    bottom = HitzSpacing.md
                ),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
            ) {
                LanguageText(
                    text = text,
                    language = language,
                    style = MaterialTheme.typography.headlineSmall,
                    hints = true,
                )
                if (onPlay != null) {
                    HitzButton(
                        text = stringResource(R.string.feature_lesson_listen),
                        onClick = onPlay,
                        style = HitzButtonStyle.Tonal,
                        size = HitzButtonSize.Small,
                        icon = HitzIcons.Speaker,
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
