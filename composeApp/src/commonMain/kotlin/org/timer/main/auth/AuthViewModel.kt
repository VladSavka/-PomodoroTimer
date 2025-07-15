package org.timer.main.auth

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.timer.main.domain.auth.*

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(AuthViewState())
    val viewState: StateFlow<AuthViewState> = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            getAuthStateUseCase().collect { authState ->
                when (authState) {
                    is AuthState.Authenticated ->
                        _viewState.update { it.copy(isLoggedIn = true, isLoading = false) }

                    AuthState.Loading ->
                        _viewState.update { it.copy(isLoading = true) }

                    AuthState.NotAuthenticated ->
                        _viewState.update { it.copy(isLoggedIn = false, isLoading = false) }

                }
            }
        }
    }

    fun onLoginClick() = viewModelScope.launch {
        loginUseCase()
    }

}