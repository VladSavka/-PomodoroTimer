package org.timer.main.auth

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.timer.main.domain.auth.*

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(AuthViewState())
    val viewState: StateFlow<AuthViewState> = _viewState.asStateFlow()

    init {

        viewModelScope.launch {
            isLoggedInUseCase().collect { isLoggedIn ->
                
                _viewState.update { it.copy(isLoggedIn = isLoggedIn, isLoading = false) }
            }
        }
    }

    fun onLoginClick() = viewModelScope.launch {
        loginUseCase()
    }

}