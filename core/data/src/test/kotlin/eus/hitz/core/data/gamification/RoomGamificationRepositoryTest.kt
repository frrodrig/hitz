package eus.hitz.core.data.gamification

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.data.database.HitzDatabase
import eus.hitz.core.domain.gamification.Activity
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.LeagueOutcome
import eus.hitz.core.domain.gamification.StreakDayKind
import eus.hitz.core.domain.gamification.Wallet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class RoomGamificationRepositoryTest {
    private lateinit var database: HitzDatabase
    private lateinit var repository: RoomGamificationRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), HitzDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomGamificationRepository(database)
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun aNewPlayerStartsWithTheDefaultWallet() = runTest {
        val data = repository.observe().first()
        assertThat(data.wallet).isEqualTo(Wallet())
        assertThat(data.activities).isEmpty()
    }

    @Test
    fun activitiesAndStreakDaysAreStored() = runTest {
        val activity = Activity("u01-1.l1.s1", ActivityKind.Lesson, 1f, true, 15, completedAt = 10, epochDay = 20_000)
        repository.recordActivity(activity)
        repository.addStreakDays(mapOf(20_000L to StreakDayKind.Practiced, 19_999L to StreakDayKind.Frozen))
        val data = repository.observe().first()
        assertThat(data.activities).containsExactly(activity)
        assertThat(data.streakDays).containsExactly(20_000L, StreakDayKind.Practiced, 19_999L, StreakDayKind.Frozen)
    }

    @Test
    fun walletAndLeagueUpdatesArePersisted() = runTest {
        repository.updateWallet { it.copy(gems = 99, streakFreezes = 1) }
        repository.updateLeague {
            it.copy(tier = 3, weekStart = 20_000, lastRank = 2, lastOutcome = LeagueOutcome.Promoted)
        }
        val data = repository.observe().first()
        assertThat(data.wallet.gems).isEqualTo(99)
        assertThat(data.wallet.streakFreezes).isEqualTo(1)
        assertThat(data.league.tier).isEqualTo(3)
        assertThat(data.league.lastOutcome).isEqualTo(LeagueOutcome.Promoted)
    }

    @Test
    fun aRewardIsClaimedOnce() = runTest {
        assertThat(repository.claim("quest:1:a", gems = 10, claimedAt = 0)).isTrue()
        assertThat(repository.claim("quest:1:a", gems = 10, claimedAt = 1)).isFalse()
        val data = repository.observe().first()
        assertThat(data.wallet.gems).isEqualTo(Wallet.INITIAL_GEMS + 10)
        assertThat(data.claimed).containsExactly("quest:1:a")
    }
}
