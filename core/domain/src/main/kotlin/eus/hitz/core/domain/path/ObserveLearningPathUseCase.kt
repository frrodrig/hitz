package eus.hitz.core.domain.path

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.model.CourseId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/** The learning path, updated whenever the progress changes. */
class ObserveLearningPathUseCase @Inject constructor(
    private val courses: CourseRepository,
    private val progress: ProgressRepository,
) {
    operator fun invoke(courseId: CourseId = CourseId.BASQUE_FROM_SPANISH): Flow<LearningPath> {
        val content = flow {
            val course = courses.getCourse(courseId)
            val units = course.sections.flatMap { it.units }.associateWith { courses.getUnit(courseId, it) }
            emit(course to units)
        }
        return combine(content, progress.observeCompleted()) { (course, units), completed ->
            LearningPathBuilder.build(course, units, completed)
        }
    }
}
