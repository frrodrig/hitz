package eus.hitz.feature.leaderboard

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.gamification.ObserveLeagueUseCase
import eus.hitz.core.testing.FakeGamificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class LeaderboardViewModelTest {
    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `the learner is ranked in a full group`() = runTest {
        val repository = FakeGamificationRepository()
        val vm = LeaderboardViewModel(ObserveLeagueUseCase(repository, repository.clock))
        vm.state.test {
            var state = awaitItem()
            if (state is LeaderboardUiState.Loading) state = awaitItem()
            val league = (state as LeaderboardUiState.Ready).league
            assertThat(league.standings).hasSize(15)
            assertThat(league.standings.count { it.isLearner }).isEqualTo(1)
            assertThat(league.tier).isEqualTo(0)
        }
    }
}
