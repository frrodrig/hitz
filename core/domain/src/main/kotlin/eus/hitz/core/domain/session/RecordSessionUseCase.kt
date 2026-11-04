package eus.hitz.core.domain.session

import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.CompleteSessionUseCase
import eus.hitz.core.domain.gamification.SessionReward
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.time.EpochClock
import javax.inject.Inject

/**
 * Saves the outcome of a session: every mistake, and when it is finished, the completion and its
 * XP, streak day and rewards. Returns what a finished session earned, or null.
 */
class RecordSessionUseCase @Inject constructor(
    private val progress: ProgressRepository,
    private val clock: EpochClock,
    private val completeSession: CompleteSessionUseCase,
) {
    suspend operator fun invoke(
        unitId: String,
        sessionId: String,
        result: LessonResult,
        kind: ActivityKind
    ): SessionReward? {
        val now = clock.now()
        if (result.mistakes.isNotEmpty()) {
            progress.recordMistakes(unitId, result.mistakes, now)
        }
        if (!result.completed) return null
        progress.recordCompletion(unitId, sessionId, result.accuracy, now)
        val perfect = result.exerciseCount > 0 && result.firstTryCorrect == result.exerciseCount
        return completeSession(sessionId, kind, result.accuracy, perfect)
    }
}
