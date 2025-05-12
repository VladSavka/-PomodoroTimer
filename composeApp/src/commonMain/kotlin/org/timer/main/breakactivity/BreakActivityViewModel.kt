package org.timer.main.breakactivity

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*

class BreakActivityViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(BreakActivityViewState())
    val viewState: StateFlow<BreakActivityViewState> = _viewState.asStateFlow()

    fun onActivitySelected(id: String) {
        _viewState.update {
            it.copy(
                selectedActivityId = id,
                showActivityList = id.isEmpty(),
                showBackButton = id.isNotEmpty()
            )
        }
    }

    fun onFirstLevelItemClick(item: Item) {
        _viewState.update { it.copy(selectedItem = item, showBackButton = true) }
    }

    fun onBackClick() {
        _viewState.update {
            it.copy(
                selectedItem = null,
                selectedActivityId = "",
                showActivityList = true,
                showBackButton = false
            )
        }
    }

}