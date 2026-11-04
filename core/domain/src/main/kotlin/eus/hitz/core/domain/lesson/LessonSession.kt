package eus.hitz.core.domain.lesson

/** A mistake, kept for spaced repetition (by word and by grammar structure). */
data class Mistake(
    val exerciseId: String,
    val vocabulary: List<String>,
    val structures: List<String>,
    val given: String,
    val expected: String,
)

/** Outcome of a lesson. */
data class LessonResult(
    val lessonId: String,
    val completed: Boolean,
    val exerciseCount: Int,
    val firstTryCorrect: Int,
    val heartsLeft: Int?,
    val mistakes: List<Mistake>,
) {
    val accuracy: Float get() = if (exerciseCount == 0) 1f else firstTryCorrect.toFloat() / exerciseCount
}

/** An exercise in the queue, identified by its content id. */
data class QueuedExercise(val exerciseId: String, val isRetry: Boolean)

/**
 * Lesson flow, independent of the UI:
 *
 * * exercises are asked in order; a wrong answer costs a heart (when hearts are on) and the
 *   exercise is asked again at the end of the lesson, until it is answered correctly;
 * * a skipped exercise (e.g. speaking when the learner cannot talk) leaves the lesson without
 *   counting towards progress or accuracy;
 * * progress only grows with correct answers;
 * * running out of hearts ends the lesson as not completed.
 */
data class LessonSession(
    val lessonId: String,
    val queue: List<QueuedExercise>,
    val position: Int = 0,
    val hearts: Int?,
    val solved: Set<String> = emptySet(),
    val firstTryCorrect: Int = 0,
    val mistakes: List<Mistake> = emptyList(),
    val skipped: Set<String> = emptySet(),
) {
    private val exerciseCount: Int = queue.count { !it.isRetry && it.exerciseId !in skipped }

    val current: QueuedExercise? get() = queue.getOrNull(position).takeIf { !isOver }

    val isOutOfHearts: Boolean get() = hearts != null && hearts <= 0

    val isOver: Boolean get() = isOutOfHearts || position >= queue.size

    /** From 0 to 1: share of exercises solved. */
    val progress: Float get() = if (exerciseCount == 0) 1f else solved.size.toFloat() / exerciseCount

    fun answerCorrect(): LessonSession {
        val item = checkNotNull(current) { "the lesson is over" }
        return copy(
            position = position + 1,
            solved = solved + item.exerciseId,
            firstTryCorrect = firstTryCorrect + if (item.isRetry) 0 else 1,
        )
    }

    fun answerIncorrect(mistake: Mistake): LessonSession {
        val item = checkNotNull(current) { "the lesson is over" }
        return copy(
            queue = queue + item.copy(isRetry = true),
            position = position + 1,
            hearts = hearts?.minus(1),
            mistakes = mistakes + mistake,
        )
    }

    /** Moves on without an answer: the exercise no longer counts and is not asked again. */
    fun skip(): LessonSession {
        val item = checkNotNull(current) { "the lesson is over" }
        return copy(
            queue = queue.filterIndexed { index, queued -> index <= position || queued.exerciseId != item.exerciseId },
            position = position + 1,
            skipped = skipped + item.exerciseId,
        )
    }

    /** Records a mistake that costs no heart and is not asked again (e.g. a wrong pair). */
    fun recordMistake(mistake: Mistake): LessonSession = copy(mistakes = mistakes + mistake)

    fun result(): LessonResult = LessonResult(
        lessonId = lessonId,
        completed = isOver && !isOutOfHearts,
        exerciseCount = exerciseCount,
        firstTryCorrect = firstTryCorrect,
        heartsLeft = hearts,
        mistakes = mistakes,
    )

    companion object {
        fun start(lessonId: String, exerciseIds: List<String>, hearts: Int?): LessonSession =
            LessonSession(lessonId, exerciseIds.map { QueuedExercise(it, isRetry = false) }, hearts = hearts)
    }
}
