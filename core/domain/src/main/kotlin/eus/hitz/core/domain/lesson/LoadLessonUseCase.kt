package eus.hitz.core.domain.lesson

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Lesson
import eus.hitz.core.model.content.Level
import javax.inject.Inject

/** A lesson with the unit it belongs to. */
data class LessonContent(val unit: CourseUnit, val lesson: Lesson)

/** Loads one authored lesson of a unit. */
class LoadLessonUseCase @Inject constructor(
    private val repository: CourseRepository,
) {
    suspend operator fun invoke(courseId: CourseId, unitId: String, lessonId: String): LessonContent {
        val unit = repository.getUnit(courseId, unitId)
        val lesson = unit.levels
            .filterIsInstance<Level.Lessons>()
            .flatMap { it.lessons }
            .firstOrNull { it.id == lessonId }
            ?: throw NoSuchElementException("lesson $lessonId not found in unit $unitId")
        return LessonContent(unit, lesson)
    }
}
