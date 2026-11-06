package eus.hitz.core.domain.content

import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Glossary

/** Read access to course content. Content ships with the app, so this never needs the network. */
interface CourseRepository {
    suspend fun getCourse(courseId: CourseId): Course

    suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit

    /** Hints for the course's Basque words; empty when a course has none. */
    suspend fun getGlossary(courseId: CourseId): Glossary = Glossary.Empty
}
