package eus.hitz.feature.shop

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.gamification.BuyItemUseCase
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import eus.hitz.core.domain.gamification.PurchaseResult
import eus.hitz.core.domain.gamification.ShopItem
import eus.hitz.core.testing.FakeGamificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ShopViewModelTest {
    private val repository = FakeGamificationRepository()
    private val vm by lazy {
        ShopViewModel(ObservePlayerUseCase(repository, repository.clock), BuyItemUseCase(repository, repository.clock))
    }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `buying a freeze spends gems and reports it`() = runTest {
        vm.events.test {
            vm.buy(ShopItem.StreakFreeze)
            assertThat(awaitItem()).isEqualTo(PurchaseEvent(ShopItem.StreakFreeze, PurchaseResult.Bought))
            vm.buy(ShopItem.StreakFreeze)
            assertThat(awaitItem().result).isEqualTo(PurchaseResult.NotEnoughGems)
        }
        assertThat(repository.data.value.wallet.streakFreezes).isEqualTo(1)
        assertThat(repository.data.value.wallet.gems).isEqualTo(0)
    }
}
