package eus.hitz.feature.path.intro

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Guidebook
import eus.hitz.core.model.content.GuidebookSection
import eus.hitz.core.model.content.KeyWord
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.core.testing.FakeAudioProvider
import eus.hitz.core.testing.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class UnitIntroViewModelTest {
    private val text = LocalizedText("Comida", "Janaria")
    private val unit = CourseUnit(
        schemaVersion = 1,
        id = "u05-1",
        section = "s2",
        bookUnit = 5,
        title = text,
        goals = listOf(LocalizedText("Pedir comida.", "Janaria eskatu.")),
        keyWords = listOf(KeyWord("ogi", "pan", "food_bread.webp"), KeyWord("ur", "agua")),
        guidebook = Guidebook(listOf(GuidebookSection(LocalizedText("Gustatu", "Gustatu"), text))),
        levels = emptyList(),
    )
    private val courses = object : CourseRepository {
        override suspend fun getCourse(courseId: CourseId): Course = error("unused")

        override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = unit
    }
    private val settings = FakeSettingsRepository()
    private val audio = FakeAudioProvider()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = UnitIntroViewModel(
        SavedStateHandle(mapOf("unitId" to "u05-1", "levelId" to "u05-1.l1", "lessonId" to "u05-1.l1.s1")),
        courses,
        settings,
        audio,
    )

    @Test
    fun `shows goals, key words with their audio and grammar, and is seen once shown`() = runTest {
        val vm = viewModel()
        val ready = vm.state.value as UnitIntroUiState.Ready

        assertThat(ready.goals).containsExactly(LocalizedText("Pedir comida.", "Janaria eskatu."))
        assertThat(ready.keyWords).containsExactly(
            KeyWordUi("ogi", "pan", "file:///android_asset/images/food_bread.webp", "u05-1.kw0"),
            KeyWordUi("ur", "agua", null, "u05-1.kw1"),
        ).inOrder()
        assertThat(ready.grammar).containsExactly(LocalizedText("Gustatu", "Gustatu"))
        assertThat(settings.settings.value.seenUnitIntros).containsExactly("u05-1")

        vm.play(ready.keyWords[1])
        assertThat(audio.played).containsExactly("u05-1.kw1")
    }
}
