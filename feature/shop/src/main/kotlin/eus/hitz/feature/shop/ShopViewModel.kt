package eus.hitz.feature.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.gamification.BuyItemUseCase
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import eus.hitz.core.domain.gamification.PlayerSummary
import eus.hitz.core.domain.gamification.PurchaseResult
import eus.hitz.core.domain.gamification.ShopItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Balance and stock shown by the shop. [nextHeartAt] is null when hearts are full. */
data class ShopUiState(
    val gems: Int = 0,
    val hearts: Int = 0,
    val nextHeartAt: Long? = null,
    val streakFreezes: Int = 0,
) {
    companion object {
        fun of(
            player: PlayerSummary
        ) = ShopUiState(player.gems, player.hearts, player.nextHeartAt, player.streakFreezes)
    }
}

/** The result of a purchase, shown once. */
data class PurchaseEvent(val item: ShopItem, val result: PurchaseResult)

@HiltViewModel
class ShopViewModel @Inject constructor(
    observePlayer: ObservePlayerUseCase,
    private val buyItem: BuyItemUseCase,
) : ViewModel() {
    val state: StateFlow<ShopUiState> = observePlayer()
        .map { ShopUiState.of(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ShopUiState())

    private val _events = Channel<PurchaseEvent>(Channel.BUFFERED)
    val events: Flow<PurchaseEvent> = _events.receiveAsFlow()

    fun buy(item: ShopItem) {
        viewModelScope.launch { _events.send(PurchaseEvent(item, buyItem(item))) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
