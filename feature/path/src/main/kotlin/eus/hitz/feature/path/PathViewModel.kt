package eus.hitz.feature.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.ObserveLearningPathUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface PathUiState {
    data object Loading : PathUiState

    data object Error : PathUiState

    data class Ready(val path: LearningPath) : PathUiState
}

@HiltViewModel
class PathViewModel @Inject constructor(
    observeLearningPath: ObserveLearningPathUseCase,
) : ViewModel() {
    val state: StateFlow<PathUiState> = observeLearningPath()
        .map<LearningPath, PathUiState> { PathUiState.Ready(it) }
        .catch { emit(PathUiState.Error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PathUiState.Loading)

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
