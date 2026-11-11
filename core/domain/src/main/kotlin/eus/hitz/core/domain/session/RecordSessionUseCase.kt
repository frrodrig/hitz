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
        val perfect = result.exerciseCount > 0 && result.firstTryCorrect == result.exerciseCount
        return completeSession(sessionId, kind, result.accuracy, perfect, activeMillis)
    }
}
