@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
 * Bottom panel shown after checking an answer: a surface-container sheet with a Material 3
 * [ListItem] whose icon sits in an expressive shape, and an expressive button. It is a polite live
 * region, so TalkBack reads the result and the correct answer without moving focus.
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
    val badgeShape = if (kind == FeedbackKind.Incorrect) MaterialShapes.Cookie4Sided else MaterialShapes.Cookie9Sided
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.extraLarge.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(HitzSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.md),
        ) {
            ListItem(
                modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
                leadingContent = {
                    Surface(shape = badgeShape.toShape(), color = colors.container, modifier = Modifier.size(48.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(
                                    if (kind == FeedbackKind.Incorrect) HitzIcons.Close else HitzIcons.Check,
                                ),
                                contentDescription = null,
                                tint = colors.onContainer,
                            )
                        }
                    }
                },
                headlineContent = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        modifier = Modifier.semantics { heading() },
                    )
                },
                supportingContent = message?.let { { Text(it, style = MaterialTheme.typography.bodyLarge) } },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
            HitzButton(
                text = continueLabel,
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                size = HitzButtonSize.Medium,
            )
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
