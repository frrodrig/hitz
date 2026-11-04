package eus.hitz.core.domain.session

import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.time.EpochClock
import javax.inject.Inject

/** Saves the outcome of a session: completion (if finished) and every mistake. */
class RecordSessionUseCase @Inject constructor(
    private val progress: ProgressRepository,
    private val clock: EpochClock,
) {
    suspend operator fun invoke(unitId: String, sessionId: String, result: LessonResult) {
        val now = clock.now()
        if (result.mistakes.isNotEmpty()) {
            progress.recordMistakes(unitId, result.mistakes, now)
        }
        if (result.completed) {
            progress.recordCompletion(unitId, sessionId, result.accuracy, now)
        }
    }
}
