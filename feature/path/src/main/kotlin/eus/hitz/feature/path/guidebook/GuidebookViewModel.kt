package eus.hitz.feature.path.guidebook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.CourseUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GuidebookUiState {
    data object Loading : GuidebookUiState

    data object Error : GuidebookUiState

    data class Ready(val unit: CourseUnit) : GuidebookUiState
}

@HiltViewModel
class GuidebookViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    courses: CourseRepository,
) : ViewModel() {
    private val unitId: String = checkNotNull(savedStateHandle["unitId"])

    private val _state = MutableStateFlow<GuidebookUiState>(GuidebookUiState.Loading)
    val state: StateFlow<GuidebookUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = runCatching { courses.getUnit(CourseId.BASQUE_FROM_SPANISH, unitId) }
                .fold(onSuccess = { GuidebookUiState.Ready(it) }, onFailure = { GuidebookUiState.Error })
        }
    }
}
