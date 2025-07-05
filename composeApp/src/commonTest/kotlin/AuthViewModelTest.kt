package org.timer.main.breakactivity

import com.varabyte.truthish.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.timer.main.auth.*
import org.timer.main.breakactivity.org.timer.main.domain.auth.*
import org.timer.main.domain.auth.*
import kotlin.test.*

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val authGateway = FakeAuthGateway()
        viewModel = AuthViewModel(
            DefaultLoginUseCase(authGateway),
            DefaultIsLoggedInUseCase(authGateway)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be user is not logged in`() = runTest {
        val initialState = viewModel.viewState.first()
        assertThat(initialState.isLoggedIn).isFalse()
    }

    @Test
    fun `user click login should update state to user is logged in`() = runTest {
        viewModel.onLoginClick()
        advanceUntilIdle()
        val viewState = viewModel.viewState.value
        assertThat(viewState.isLoggedIn).isTrue()
    }
}