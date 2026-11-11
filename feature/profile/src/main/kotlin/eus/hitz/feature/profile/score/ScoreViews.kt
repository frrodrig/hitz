@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.profile.score

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.domain.score.Score
import eus.hitz.core.domain.score.ScoreBand
import eus.hitz.core.domain.score.ScorePoint
import eus.hitz.core.domain.score.ScoreRules
import eus.hitz.feature.profile.R

private const val PERCENT = 100
private val CHART_HEIGHT = 180.dp
private val LINE_WIDTH = 3.dp
private val DOT_RADIUS = 5.dp

/** The limits of the bands and the top. */
private val GRID_LINES = ScoreBand.entries.drop(1).map { it.range.first } + ScoreRules.MAX
private val DASH = floatArrayOf(8f, 8f)

internal val ScoreBand.title: Int
    get() = when (this) {
        ScoreBand.FirstSteps -> R.string.feature_profile_score_band_first_steps
        ScoreBand.Basics -> R.string.feature_profile_score_band_basics
        ScoreBand.Confident -> R.string.feature_profile_score_band_confident
    }

internal val ScoreBand.description: Int
    get() = when (this) {
        ScoreBand.FirstSteps -> R.string.feature_profile_score_band_first_steps_body
        ScoreBand.Basics -> R.string.feature_profile_score_band_basics_body
        ScoreBand.Confident -> R.string.feature_profile_score_band_confident_body
    }

/** The score, its band and the way to the next point. */
@Composable
internal fun ScoreSummary(score: Score, modifier: Modifier = Modifier) {
    val spoken = stringResource(R.string.feature_profile_score_spoken, score.value, ScoreRules.MAX)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm)) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
            Text(
                text = score.value.toString(),
                style = MaterialTheme.typography.displayLargeEmphasized,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clearAndSetSemantics { contentDescription = spoken },
            )
            Column(verticalArrangement = Arrangement.spacedBy(HitzSpacing.xs)) {
                Text(
                    text = stringResource(R.string.feature_profile_score_scale, ScoreRules.MAX),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(stringResource(score.band.title), style = MaterialTheme.typography.titleMediumEmphasized)
            }
        }
        if (score.isTop) {
            Text(stringResource(R.string.feature_profile_score_top), style = MaterialTheme.typography.bodyMedium)
        } else {
            val next = score.value + 1
            Text(
                text = stringResource(
                    R.string.feature_profile_score_next,
                    next,
                    (score.progressToNext * PERCENT).toInt(),
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            LinearWavyProgressIndicator(
                progress = { score.progressToNext },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (score.gainedThisWeek > 0) {
            Text(
                text = pluralStringResource(
                    R.plurals.feature_profile_score_week,
                    score.gainedThisWeek,
                    score.gainedThisWeek,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/**
 * The score over time as a stepped line, with guides at the band limits. The line is decorative
 * for TalkBack: a sentence says where the score started and where it is now.
 */
@Composable
internal fun ScoreChart(history: List<ScorePoint>, modifier: Modifier = Modifier) {
    val first = history.first()
    val last = history.last()
    val description = stringResource(R.string.feature_profile_score_chart_spoken, first.value, last.value)
    val measurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    val line = MaterialTheme.colorScheme.primary
    val grid = MaterialTheme.colorScheme.outlineVariant
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(CHART_HEIGHT)
                .clearAndSetSemantics { contentDescription = description },
        ) {
            val inset = 16.dp.toPx()
            val labelWidth = 24.dp.toPx()
            val left = inset + labelWidth
            val right = size.width - inset
            val top = inset
            val bottom = size.height - inset
            fun y(value: Int) = bottom - (bottom - top) * value / ScoreRules.MAX
            val days = (last.epochDay - first.epochDay).coerceAtLeast(1)
            fun x(day: Long) = left + (right - left) * (day - first.epochDay) / days

            GRID_LINES.forEach { value ->
                drawLine(
                    color = grid,
                    start = Offset(left, y(value)),
                    end = Offset(right, y(value)),
                    pathEffect = PathEffect.dashPathEffect(DASH),
                )
                val label = measurer.measure(value.toString(), labelStyle)
                drawText(label, topLeft = Offset(inset, y(value) - label.size.height / 2f))
            }
            drawLine(grid, Offset(left, bottom), Offset(right, bottom))

            val path = Path().apply {
                moveTo(if (history.size == 1) left else x(first.epochDay), y(first.value))
                history.drop(1).forEach { point ->
                    lineTo(x(point.epochDay), y(point.value))
                }
                if (history.size == 1) lineTo(right, y(first.value))
            }
            drawPath(path, line, style = Stroke(LINE_WIDTH.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawCircle(line, DOT_RADIUS.toPx(), Offset(right, y(last.value)))
        }
    }
}
