package eus.hitz.core.domain.gamification

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.testing.FakeGamificationRepository
import eus.hitz.core.domain.testing.TestClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GamificationUseCasesTest {
    private val repository = FakeGamificationRepository()
    private val clock = TestClock()
    private val grant = GrantRewardsUseCase(repository, clock.gameClock)
    private val complete = CompleteSessionUseCase(repository, clock.gameClock, grant)
    private val refresh = RefreshPlayerUseCase(repository, clock.gameClock, grant)
    private val hearts = HeartsUseCase(repository, clock.gameClock)
    private val buy = BuyItemUseCase(repository, clock.gameClock)
    private val summary get() = repository.data.value.summary(clock.now, clock.today)

    @Test
    fun `a first session earns XP and starts the streak`() = runTest {
        val reward = complete("l1", ActivityKind.Lesson, accuracy = 1f, perfect = true)
        assertThat(reward.xp).isEqualTo(15)
        assertThat(reward.streak).isEqualTo(1)
        assertThat(reward.streakExtended).isTrue()
        assertThat(summary.totalXp).isEqualTo(15)
        assertThat(summary.practicedToday).isTrue()

        val second = complete("l2", ActivityKind.Lesson, accuracy = 0.5f, perfect = false)
        assertThat(second.streakExtended).isFalse()
        assertThat(second.streak).isEqualTo(1)
    }

    @Test
    fun `quests and achievements grant their gems once`() = runTest {
        val reward = complete("l1", ActivityKind.Lesson, accuracy = 1f, perfect = true)
        // The first perfect session reaches the first "perfect" achievement tier.
        assertThat(reward.rewards).contains(Reward.AchievementTier(AchievementType.Perfect, 1))
        val gems = repository.data.value.wallet.gems
        assertThat(gems).isEqualTo(Wallet.INITIAL_GEMS + reward.rewards.sumOf { it.gems })
        assertThat(grant()).isEmpty()
        assertThat(repository.data.value.wallet.gems).isEqualTo(gems)
    }

    @Test
    fun `daily quests are granted when done`() = runTest {
        repeat(4) { complete("l$it", ActivityKind.Lesson, accuracy = 1f, perfect = true) }
        complete("story", ActivityKind.Story, accuracy = 1f, perfect = true)
        complete("review", ActivityKind.Review, accuracy = 1f, perfect = true)
        assertThat(summary.quests.all { it.isDone && it.claimed }).isTrue()
    }

    @Test
    fun `reviews give a heart back`() = runTest {
        hearts.lose()
        hearts.lose()
        assertThat(hearts.current()).isEqualTo(3)
        complete("review", ActivityKind.Review, accuracy = 1f, perfect = false)
        assertThat(hearts.current()).isEqualTo(4)
    }

    @Test
    fun `hearts regenerate with time`() = runTest {
        hearts.lose()
        clock.advanceHours(4)
        assertThat(hearts.current()).isEqualTo(5)
    }

    @Test
    fun `a freeze saves the streak over a missed day`() = runTest {
        repository.updateWallet { it.copy(streakFreezes = 1) }
        complete("l1", ActivityKind.Lesson, accuracy = 1f, perfect = false)
        clock.advanceDays(2)
        refresh()
        assertThat(summary.streak).isEqualTo(1)
        assertThat(summary.streakFreezes).isEqualTo(0)
        clock.advanceDays(1)
        refresh()
        assertThat(summary.streak).isEqualTo(0)
    }

    @Test
    fun `shop items cost gems and are refused when not needed`() = runTest {
        assertThat(buy(ShopItem.HeartRefill)).isEqualTo(PurchaseResult.NotNeeded)
        hearts.lose()
        assertThat(buy(ShopItem.HeartRefill)).isEqualTo(PurchaseResult.NotEnoughGems)
        assertThat(buy(ShopItem.StreakFreeze)).isEqualTo(PurchaseResult.Bought)
        assertThat(repository.data.value.wallet.gems).isEqualTo(Wallet.INITIAL_GEMS - ShopItem.StreakFreeze.price)
        assertThat(repository.data.value.wallet.streakFreezes).isEqualTo(1)
    }

    @Test
    fun `the league week closes with promotion and rank gems`() = runTest {
        refresh()
        val joined = repository.data.value.league
        assertThat(joined.weekStart).isEqualTo(Leagues.weekStart(clock.today))
        repeat(20) { complete("legendary$it", ActivityKind.Legendary, accuracy = 1f, perfect = true) }
        val gemsBefore = repository.data.value.wallet.gems
        clock.advanceDays(7)
        refresh()
        val league = repository.data.value.league
        assertThat(league.tier).isEqualTo(1)
        assertThat(league.lastRank).isEqualTo(1)
        assertThat(league.lastOutcome).isEqualTo(LeagueOutcome.Promoted)
        assertThat(repository.data.value.wallet.gems - gemsBefore).isAtLeast(Leagues.gemsForRank(1))
    }

    @Test
    fun `the league view ranks the learner with this week's XP`() = runTest {
        refresh()
        complete("l1", ActivityKind.Lesson, accuracy = 1f, perfect = false)
        val view = ObserveLeagueUseCase(repository, clock.gameClock)().first()
        assertThat(view.standings.single { it.isLearner }.xp).isEqualTo(10)
        assertThat(view.daysLeft).isEqualTo(5)
    }
}
