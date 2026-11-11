package eus.hitz.core.domain.gamification

import eus.hitz.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * What a session added: today's goal before and after it, and the achievement tiers it reached.
 * [goalReached] is true for the session that completes the day's goal.
 */
data class SessionReward(
    val before: DailyProgress,
    val after: DailyProgress,
    val achievements: List<AchievementTier> = emptyList(),
) {
    val goalReached: Boolean get() = !before.goalMet && after.goalMet
}

class ObservePlayerUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val settings: SettingsRepository,
    private val clock: GameClock,
) {
    operator fun invoke(): Flow<PlayerSummary> = combine(repository.observe(), settings.settings) { data, preferences ->
        data.summary(clock.today(), preferences.dailyGoalMinutes)
    }
}

/**
 * Adds study time to today, also for sessions left before the end (the practice counts even if
 * the lesson does not). Returns today's goal before and after.
 */
class RecordStudyTimeUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val settings: SettingsRepository,
    private val clock: GameClock,
) {
    suspend operator fun invoke(activeMillis: Long): Pair<DailyProgress, DailyProgress> {
        val today = clock.today()
        val goalMinutes = settings.settings.first().dailyGoalMinutes
        val before = repository.observe().first().studyDays[today]?.activeMillis ?: 0
        val day = repository.addStudyTime(today, activeMillis, DailyGoalRules.goalMillis(goalMinutes))
        return DailyProgress(before, goalMinutes) to DailyProgress(day.activeMillis, goalMinutes)
    }
}

/** Saves a finished session with its study time; returns what it added to the goal and badges. */
class CompleteSessionUseCase @Inject constructor(
    private val repository: GamificationRepository,
    private val clock: GameClock,
    private val recordStudyTime: RecordStudyTimeUseCase,
) {
    suspend operator fun invoke(
        sessionId: String,
        kind: ActivityKind,
        accuracy: Float,
        perfect: Boolean,
        activeMillis: Long,
    ): SessionReward {
        val badgesBefore = Achievements.progress(repository.observe().first())
        val (before, after) = recordStudyTime(activeMillis)
        repository.recordActivity(
            Activity(sessionId, kind, accuracy, perfect, activeMillis, clock.now(), clock.today())
        )
        val badgesAfter = Achievements.progress(repository.observe().first())
        return SessionReward(before, after, Achievements.reached(badgesBefore, badgesAfter))
    }
}
