package eus.hitz.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.ColorFamily
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/** Result of checking an answer. */
enum class FeedbackKind { Correct, AlmostCorrect, Incorrect }

@Composable
private fun feedbackColors(kind: FeedbackKind): ColorFamily {
    val scheme = MaterialTheme.colorScheme
    return when (kind) {
        FeedbackKind.Correct -> HitzTheme.extendedColors.success
        FeedbackKind.AlmostCorrect -> HitzTheme.extendedColors.xp
        FeedbackKind.Incorrect ->
            ColorFamily(scheme.error, scheme.onError, scheme.errorContainer, scheme.onErrorContainer)
    }
}

/**
 * Bottom panel shown after checking an answer. It is a polite live region, so TalkBack reads the
 * result and the correct answer without the learner having to move focus.
 */
@Composable
fun HitzFeedbackPanel(
    kind: FeedbackKind,
    title: String,
    continueLabel: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    val colors = feedbackColors(kind)
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.container,
        contentColor = colors.onContainer,
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(HitzSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            Column(
                modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
                ) {
                    Icon(
                        painter = painterResource(
                            if (kind == FeedbackKind.Incorrect) HitzIcons.Close else HitzIcons.Check,
                        ),
                        contentDescription = null,
                        tint = colors.color,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() },
                    )
                }
                if (message != null) {
                    Text(message, style = MaterialTheme.typography.bodyLarge)
                }
            }
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().padding(top = HitzSpacing.xs),
                colors = ButtonDefaults.buttonColors(containerColor = colors.color, contentColor = colors.onColor),
            ) {
                Text(continueLabel, Modifier.padding(vertical = 6.dp))
            }
        }
    }
}

@HitzPreviews
@Composable
private fun HitzFeedbackPanelPreview() {
    HitzTheme {
        Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            HitzFeedbackPanel(FeedbackKind.Correct, "Oso ondo!", "Jarraitu", {})
            HitzFeedbackPanel(FeedbackKind.AlmostCorrect, "Ia-ia!", "Jarraitu", {}, message = "Ni Ane naiz.")
            HitzFeedbackPanel(FeedbackKind.Incorrect, "Erantzun zuzena:", "Ados", {}, message = "Kalean nago.")
        }
    }
}
