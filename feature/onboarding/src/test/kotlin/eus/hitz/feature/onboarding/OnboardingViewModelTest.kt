package eus.hitz.feature.onboarding

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.core.model.content.Section
import eus.hitz.core.testing.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class OnboardingViewModelTest {
    private val text = LocalizedText("Primeros pasos", "Lehen urratsak")
    private val courses = object : CourseRepository {
        override suspend fun getCourse(courseId: CourseId) =
            Course(1, "eu-es", "eu", "es", text, listOf(Section("s1", text, text, listOf("u01-1", "u01-2"))))

        override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = error("unused")
    }
    private val settings = FakeSettingsRepository()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `presents the sections and is completed once`() {
        val vm = OnboardingViewModel(courses, settings)
        assertThat(vm.sections.value).containsExactly(SectionUi(text, text, units = 2))
        assertThat(settings.settings.value.onboardingCompleted).isFalse()
        vm.complete(goalMinutes = 15)
        assertThat(settings.settings.value.onboardingCompleted).isTrue()
        assertThat(settings.settings.value.dailyGoalMinutes).isEqualTo(15)
    }
}
