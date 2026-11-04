@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.shop

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzButtonSize
import eus.hitz.core.designsystem.component.HitzStatBar
import eus.hitz.core.designsystem.component.StatKind
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.preview.HitzPreviews
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.designsystem.theme.HitzTheme
import eus.hitz.core.domain.gamification.HeartRules
import eus.hitz.core.domain.gamification.PurchaseResult
import eus.hitz.core.domain.gamification.ShopItem
import eus.hitz.core.domain.gamification.StreakRules
import java.text.DateFormat
import java.util.Date

@Composable
fun ShopScreen(
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalContext.current.resources
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            snackbarHostState.showSnackbar(resources.getString(event.message()))
        }
    }
    Scaffold(modifier = modifier, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        ShopContent(state, onBuy = viewModel::buy, modifier = Modifier.padding(padding))
    }
}

private fun PurchaseEvent.message(): Int = when (result) {
    PurchaseResult.Bought -> when (item) {
        ShopItem.HeartRefill -> R.string.feature_shop_bought_hearts
        ShopItem.StreakFreeze -> R.string.feature_shop_bought_freeze
    }
    PurchaseResult.NotEnoughGems -> R.string.feature_shop_not_enough_gems
    PurchaseResult.NotNeeded -> when (item) {
        ShopItem.HeartRefill -> R.string.feature_shop_hearts_full
        ShopItem.StreakFreeze -> R.string.feature_shop_freezes_full
    }
}

/** Gem balance and the items: refill hearts and streak freezes. */
@Composable
internal fun ShopContent(state: ShopUiState, onBuy: (ShopItem) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(HitzSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.feature_shop_title),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
            )
            HitzStatBar(listOf(StatKind.Gems to state.gems, StatKind.Hearts to state.hearts))
        }
        Text(stringResource(R.string.feature_shop_intro), style = MaterialTheme.typography.bodyLarge)
        val heartsFull = state.hearts >= HeartRules.MAX_HEARTS
        val nextHeart = state.nextHeartAt
        ShopItemCard(
            icon = HitzIcons.Heart,
            tint = HitzTheme.extendedColors.heart.color,
            title = stringResource(R.string.feature_shop_hearts_title),
            description = if (heartsFull || nextHeart == null) {
                stringResource(R.string.feature_shop_hearts_description_full)
            } else {
                stringResource(
                    R.string.feature_shop_hearts_description,
                    state.hearts,
                    HeartRules.MAX_HEARTS,
                    DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(nextHeart)),
                )
            },
            price = ShopItem.HeartRefill.price,
            enabled = !heartsFull,
            onBuy = { onBuy(ShopItem.HeartRefill) },
        )
        ShopItemCard(
            icon = HitzIcons.Streak,
            tint = HitzTheme.extendedColors.streak.color,
            title = stringResource(R.string.feature_shop_freeze_title),
            description = stringResource(
                R.string.feature_shop_freeze_description,
                state.streakFreezes,
                StreakRules.MAX_FREEZES,
            ),
            price = ShopItem.StreakFreeze.price,
            enabled = state.streakFreezes < StreakRules.MAX_FREEZES,
            onBuy = { onBuy(ShopItem.StreakFreeze) },
        )
        Text(
            text = stringResource(R.string.feature_shop_earn),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ShopItemCard(
    @DrawableRes icon: Int,
    tint: Color,
    title: String,
    description: String,
    price: Int,
    enabled: Boolean,
    onBuy: () -> Unit,
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(HitzSpacing.lg), verticalArrangement = Arrangement.spacedBy(HitzSpacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HitzSpacing.md)
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(40.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Text(description, style = MaterialTheme.typography.bodyMedium)
                }
            }
            HitzButton(
                text = pluralStringResource(R.plurals.feature_shop_buy, price, price),
                onClick = onBuy,
                enabled = enabled,
                size = HitzButtonSize.Medium,
                icon = HitzIcons.Gem,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@HitzPreviews
@Composable
private fun ShopContentPreview() {
    HitzTheme { ShopContent(ShopUiState(gems = 45, hearts = 3, nextHeartAt = 0, streakFreezes = 1), onBuy = {}) }
}
