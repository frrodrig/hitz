package eus.hitz.feature.lesson.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.model.content.Gloss
import eus.hitz.core.model.content.Glossary
import eus.hitz.core.model.content.HintedRun
import eus.hitz.core.model.content.Language
import kotlin.math.max
import kotlin.math.min

internal val Language.localeList: LocaleList
    get() = when (this) {
        Language.Basque -> LocaleList("eu")
        Language.Spanish -> LocaleList("es")
    }

/** Word hints for Basque text below this point; null turns them off (legendary levels). */
internal val LocalGlossary = staticCompositionLocalOf<Glossary?> { null }

/**
 * Text tagged with its language, so TalkBack and hyphenation use the right locale even when the
 * UI is in the other language. With [hints], Basque words that have a hint are underlined and
 * show their meaning when tapped.
 */
@Composable
internal fun LanguageText(
    text: String,
    language: Language,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    hints: Boolean = false,
) {
    val glossary = LocalGlossary.current
    val localized = style.copy(localeList = language.localeList)
    if (hints && language == Language.Basque && glossary != null) {
        HintedText(text, glossary, localized, modifier)
    } else {
        Text(text = text, modifier = modifier, style = localized)
    }
}

@Composable
private fun HintedText(text: String, glossary: Glossary, style: TextStyle, modifier: Modifier) {
    val runs = remember(text, glossary) { glossary.segment(text) }
    var selected by remember(text) { mutableStateOf<Int?>(null) }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val highlight = MaterialTheme.colorScheme.secondaryContainer
    val underline = MaterialTheme.colorScheme.outline
    val ranges = remember(runs) { hintedRanges(runs) }
    val annotated = buildAnnotatedString {
        runs.forEachIndexed { index, run ->
            if (run.gloss == null) {
                append(run.text)
            } else {
                val background = if (selected == index) highlight else Color.Unspecified
                val link = LinkAnnotation.Clickable(
                    tag = "hint$index",
                    styles = TextLinkStyles(SpanStyle(background = background)),
                ) { selected = if (selected == index) null else index }
                withLink(link) { append(run.text) }
            }
        }
    }
    Column(modifier) {
        Text(
            text = annotated,
            style = style,
            onTextLayout = { layout = it },
            modifier = Modifier.drawBehind {
                val result = layout ?: return@drawBehind
                ranges.forEach { dashedUnderline(result, it, underline) }
            },
        )
        selected?.let { index -> HintBubble(runs[index], Modifier.padding(top = HitzSpacing.sm)) }
    }
}

/** A dotted line under the characters of [range], on every line they take. */
private fun DrawScope.dashedUnderline(layout: TextLayoutResult, range: IntRange, color: Color) {
    val dash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 2.dp.toPx()))
    val first = layout.getLineForOffset(range.first)
    val last = layout.getLineForOffset(range.last)
    for (line in first..last) {
        val start = if (line == first) layout.getHorizontalPosition(range.first, true) else layout.getLineLeft(line)
        val end = if (line == last) layout.getHorizontalPosition(range.last + 1, true) else layout.getLineRight(line)
        val y = layout.getLineBaseline(line) + 3.dp.toPx()
        drawLine(color, Offset(min(start, end), y), Offset(max(start, end), y), 1.5.dp.toPx(), pathEffect = dash)
    }
}

/** Character ranges of the runs that have a hint. */
private fun hintedRanges(runs: List<HintedRun>): List<IntRange> {
    var offset = 0
    return buildList {
        for (run in runs) {
            if (run.gloss != null) add(offset until offset + run.text.length)
            offset += run.text.length
        }
    }
}

@Composable
private fun HintBubble(run: HintedRun, modifier: Modifier = Modifier) {
    val gloss = run.gloss ?: return
    Surface(
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.inverseSurface,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, localeList = Language.Basque.localeList)) {
                    append(run.text)
                }
                gloss.lemma?.let { append(" ($it)") }
                append(" · ")
                withStyle(SpanStyle(localeList = Language.Spanish.localeList)) { append(gloss.es) }
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = HitzSpacing.md, vertical = HitzSpacing.sm),
        )
    }
}

@HitzPreviews
@Composable
private fun HintedTextPreview() {
    val glossary = Glossary(entries = mapOf("egun on" to Gloss("buenos días"), "ogia" to Gloss("pan", "ogi")))
    HitzTheme {
        Surface {
            Column(Modifier.padding(HitzSpacing.lg)) {
                HintedText("Egun on! Ogia, mesedez.", glossary, MaterialTheme.typography.headlineSmall, Modifier)
                HintBubble(glossary.segment("Ogia").first(), Modifier.padding(top = HitzSpacing.sm))
            }
        }
    }
}
