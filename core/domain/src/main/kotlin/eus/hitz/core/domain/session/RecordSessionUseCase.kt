package eus.hitz.core.domain.session

import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.CompleteSessionUseCase
import eus.hitz.core.domain.gamification.RecordStudyTimeUseCase
import eus.hitz.core.domain.gamification.SessionReward
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.time.EpochClock
import javax.inject.Inject

/**
 * Saves the outcome of a session: every mistake and its study time, and when it is finished, the
 * completion. Returns what a finished session added to the daily goal, or null when unfinished.
 */
class RecordSessionUseCase @Inject constructor(
    private val progress: ProgressRepository,
    private val clock: EpochClock,
    private val completeSession: CompleteSessionUseCase,
    private val recordStudyTime: RecordStudyTimeUseCase,
) {
    suspend operator fun invoke(
        unitId: String,
        sessionId: String,
        result: LessonResult,
        kind: ActivityKind,
        activeMillis: Long,
    ): SessionReward? {
        val now = clock.now()
        if (result.mistakes.isNotEmpty()) {
            progress.recordMistakes(unitId, result.mistakes, now)
        }
        if (!result.completed) {
            // Practice counts for the daily goal even when the session is left unfinished.
            if (activeMillis > 0) recordStudyTime(activeMillis)
            return null
        }
        progress.recordCompletion(unitId, sessionId, result.accuracy, now)
        return completeSession(sessionId, kind, result.accuracy, result.isPerfect, activeMillis)
    }

    /**
     * Saves a practice session from the practice hub. Its exercises may come from several units,
     * so each mistake is saved under its own unit (exercise ids start with the unit id). Practice
     * is not a step of the path: no completion is recorded, but it counts as a review for the
     * daily goal and the achievements.
     */
    suspend fun practice(sessionId: String, result: LessonResult, activeMillis: Long): SessionReward? {
        val now = clock.now()
        result.mistakes.groupBy { it.exerciseId.substringBefore('.') }.forEach { (unitId, mistakes) ->
            progress.recordMistakes(unitId, mistakes, now)
        }
        if (!result.completed) {
            if (activeMillis > 0) recordStudyTime(activeMillis)
            return null
        }
        return completeSession(sessionId, ActivityKind.Review, result.accuracy, result.isPerfect, activeMillis)
    }

    private val LessonResult.isPerfect: Boolean get() = exerciseCount > 0 && firstTryCorrect == exerciseCount
}
