package eus.hitz.core.data.content

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.ContentJson
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Glossary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

/** Loads course JSON from the bundled assets and caches the parsed result in memory. */
internal class AssetCourseRepository @Inject constructor(
    private val source: ContentSource,
    @param:Named(IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher,
) : CourseRepository {
    private val mutex = Mutex()
    private val courses = mutableMapOf<CourseId, Course>()
    private val units = mutableMapOf<Pair<CourseId, String>, CourseUnit>()
    private val glossaries = mutableMapOf<CourseId, Glossary>()

    override suspend fun getCourse(courseId: CourseId): Course = mutex.withLock {
        courses.getOrPut(courseId) { decode("$courseId/course.json") }
    }

    override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = mutex.withLock {
        units.getOrPut(courseId to unitId) { decode("$courseId/units/$unitId.json") }
    }

    override suspend fun getGlossary(courseId: CourseId): Glossary = mutex.withLock {
        glossaries.getOrPut(courseId) { decode("$courseId/glossary.json") }
    }

    private suspend inline fun <reified T> decode(path: String): T = withContext(ioDispatcher) {
        ContentJson.decodeFromString<T>(source.read(path))
    }

    companion object {
        const val IO_DISPATCHER = "io"
    }
}
