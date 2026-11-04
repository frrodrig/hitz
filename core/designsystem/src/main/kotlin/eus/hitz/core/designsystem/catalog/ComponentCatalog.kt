@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.core.designsystem.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eus.hitz.core.designsystem.component.ChoiceState
import eus.hitz.core.designsystem.component.FeedbackKind
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzButtonStyle
import eus.hitz.core.designsystem.component.HitzChoiceCard
import eus.hitz.core.designsystem.component.HitzFeedbackPanel
import eus.hitz.core.designsystem.component.HitzLessonProgressBar
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.component.HitzStatCounter
import eus.hitz.core.designsystem.component.HitzWordBank
import eus.hitz.core.designsystem.component.HitzWordChip
import eus.hitz.core.designsystem.component.StatKind
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme

/**
 * Every design-system component on one screen. Used by the screenshot tests and handy to review
 * the theme in the IDE preview.
 */
@Composable
fun ComponentCatalog(modifier: Modifier = Modifier, scrollable: Boolean = true) {
    Surface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .let { if (scrollable) it.verticalScroll(rememberScrollState()) else it }
                .padding(vertical = HitzSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = HitzSpacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md),
            ) {
                HitzMascot(size = HitzSpacing.xxxl * 2, contentDescription = null)
                Column {
                    Text("Hitz", style = MaterialTheme.typography.displaySmallEmphasized)
                    Text("Euskara pausoz pauso", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Row(
                modifier = Modifier.padding(horizontal = HitzSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                HitzStatCounter(StatKind.Streak, 12)
                HitzStatCounter(StatKind.Gems, 340)
                HitzStatCounter(StatKind.Hearts, 5)
                HitzStatCounter(StatKind.Xp, 90)
            }
            HitzLessonProgressBar(0.6f, Modifier.padding(horizontal = HitzSpacing.lg))
            Column(
                modifier = Modifier.padding(horizontal = HitzSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                Text("Zer esan nahi du «etxea»?", style = MaterialTheme.typography.titleLargeEmphasized)
                HitzChoiceCard("la casa", ChoiceState.Correct, {})
                HitzChoiceCard("la calle", ChoiceState.Default, {})
                HitzChoiceCard("la escuela", ChoiceState.Incorrect, {})
            }
            HitzWordBank(Modifier.padding(horizontal = HitzSpacing.lg)) {
                listOf("Ni", "Ane", "naiz", "zu", "Bilbokoa").forEachIndexed { index, word ->
                    HitzWordChip(word, onClick = {}, used = index == 0)
                }
            }
            Column(
                modifier = Modifier.padding(horizontal = HitzSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
            ) {
                HitzButton("Egiaztatu", {}, Modifier.fillMaxWidth())
                HitzButton("Entzun", {}, Modifier.fillMaxWidth(), HitzButtonStyle.Outlined, icon = HitzIcons.Speaker)
            }
            HitzFeedbackPanel(FeedbackKind.Incorrect, "Erantzun zuzena:", "Ados", {}, message = "Kalean nago.")
        }
    }
}

@HitzPreviews
@Composable
private fun ComponentCatalogPreview() {
    HitzTheme { ComponentCatalog() }
}
