package eus.hitz.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.LocalizedText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** A section of the course, as presented in "what you will learn". */
data class SectionUi(val title: LocalizedText, val description: LocalizedText, val units: Int)

/** The first-run introduction: what Hitz is, what the course covers, how it works and your level. */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    courses: CourseRepository,
    private val settings: SettingsRepository,
) : ViewModel() {
    private val _sections = MutableStateFlow<List<SectionUi>>(emptyList())
    val sections: StateFlow<List<SectionUi>> = _sections.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { courses.getCourse(CourseId.BASQUE_FROM_SPANISH) }.onSuccess { course ->
                _sections.value = course.sections.map { SectionUi(it.title, it.description, it.units.size) }
            }
        }
    }

    /** The introduction is shown once, whether finished or skipped. */
    fun complete() {
        viewModelScope.launch { settings.setOnboardingCompleted() }
    }
}
