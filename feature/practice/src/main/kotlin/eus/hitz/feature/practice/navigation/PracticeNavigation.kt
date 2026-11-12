package eus.hitz.feature.practice.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import eus.hitz.core.domain.practice.PracticeMode
import eus.hitz.feature.practice.PracticeScreen
import eus.hitz.feature.practice.UnitReviewScreen
import kotlinx.serialization.Serializable

/** The practice hub, a top-level destination. */
@Serializable
data object PracticeRoute

/** Review of one unit; the argument name is read by [eus.hitz.feature.practice.UnitReviewViewModel]. */
@Serializable
data class UnitReviewRoute(val unitId: String)

fun NavController.navigateToUnitReview(unitId: String, navOptions: NavOptions? = null) =
    navigate(UnitReviewRoute(unitId), navOptions)

fun NavGraphBuilder.practiceScreen(onPractice: (PracticeMode) -> Unit, onOpenUnit: (unitId: String) -> Unit) {
    composable<PracticeRoute> { PracticeScreen(onPractice = onPractice, onOpenUnit = onOpenUnit) }
}

fun NavGraphBuilder.unitReviewScreen(
    onBack: () -> Unit,
    onPractice: (unitId: String) -> Unit,
    onReadStory: (unitId: String, levelId: String) -> Unit,
) {
    composable<UnitReviewRoute> {
        UnitReviewScreen(onBack = onBack, onPractice = onPractice, onReadStory = onReadStory)
    }
}
