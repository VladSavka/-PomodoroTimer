package org.timer.main.video

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*

class BreakActivityViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(BreakActivityViewState())
    val viewState: StateFlow<BreakActivityViewState> = _viewState.asStateFlow()

    fun onActivitySelected(id: String) {
        _viewState.update { it.copy(selectedActivityId = id, show = id.isEmpty()) }
    }


}