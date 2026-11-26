@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.path

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.path.PathRecall
import eus.hitz.core.domain.path.PathSection

private val RecallIconBox = 48.dp
private val RecallIcon = 26.dp

/**
 * The cumulative review after a textbook unit: what it is, that it is optional, and a button once
 * the unit is finished. It sits between units, off the track, because it never blocks the way.
 */
@Composable
internal fun RecallCard(recall: PathRecall, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (container, iconBox, iconTint) = if (recall.isAvailable) {
        Triple(scheme.secondaryContainer, scheme.secondary, scheme.onSecondary)
    } else {
        Triple(scheme.surfaceContainerHigh, scheme.surfaceContainerHighest, scheme.onSurfaceVariant)
    }
    Surface(shape = MaterialTheme.shapes.large, color = container, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
            ) {
                Surface(shape = CircleShape, color = iconBox, modifier = Modifier.size(RecallIconBox)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painterResource(if (recall.isDone) HitzIcons.Check else HitzIcons.Recall),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(RecallIcon),
                        )
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(HitzSpacing.xxs)) {
                    Text(
                        text = stringResource(R.string.feature_path_recall),
                        style = MaterialTheme.typography.titleMediumEmphasized,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = stringResource(
                            when {
                                !recall.isAvailable -> R.string.feature_path_recall_locked
                                recall.isDone -> R.string.feature_path_recall_done
                                else -> R.string.feature_path_recall_body
                            },
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
            if (recall.isAvailable) {
                FilledTonalButton(
                    onClick = onClick,
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val label = if (recall.isDone) {
                        R.string.feature_path_recall_again
                    } else {
                        R.string.feature_path_recall_start
                    }
                    Text(stringResource(label))
                }
            }
        }
    }
}

/**
 * The end of a section: the next one, with a way into it. Once it is unlocked the button opens it;
 * before that, a learner who already knows what is left can take a test and jump to it.
 */
@Composable
internal fun NextSectionCard(
    section: PathSection,
    number: Int,
    onOpen: () -> Unit,
    onJump: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(HitzSpacing.xl), verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
            Text(
                text = stringResource(R.string.feature_path_next_section, number, section.level),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = section.title.resolve(),
                style = MaterialTheme.typography.headlineSmallEmphasized,
                modifier = Modifier.semantics { heading() },
            )
            Text(section.description.resolve(), style = MaterialTheme.typography.bodyMedium)
            if (section.isUnlocked) {
                Button(
                    onClick = onOpen,
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.fillMaxWidth().padding(top = HitzSpacing.sm),
                ) {
                    Text(stringResource(R.string.feature_path_next_open, number))
                }
            } else {
                Text(
                    text = stringResource(R.string.feature_path_next_locked),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = HitzSpacing.xs),
                )
                FilledTonalButton(
                    onClick = onJump,
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.feature_path_jump))
                }
            }
        }
    }
}

@HitzPreviews
@Composable
private fun SectionRowsPreview() {
    HitzTheme {
        Surface {
            Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg)) {
                RecallCard(PathRecall("u02-2", "u02-2.recall", isAvailable = true, isDone = false), onClick = {})
                RecallCard(PathRecall("u03-2", "u03-2.recall", isAvailable = false, isDone = false), onClick = {})
                NextSectionCard(PathPreviewData.nextSection, number = 2, onOpen = {}, onJump = {})
            }
        }
    }
}
