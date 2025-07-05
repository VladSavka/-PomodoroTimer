package org.timer.main.breakactivity.org.timer.main.domain.auth

import com.varabyte.truthish.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.timer.main.domain.auth.*
import kotlin.test.*

class LoginUseCaseTest {

    private lateinit var loginUseCase: DefaultLoginUseCase
    private lateinit var authGetway: FakeAuthGateway

    @BeforeTest
    fun setUp() {
        authGetway = FakeAuthGateway()
        loginUseCase = DefaultLoginUseCase(authGetway)
    }

    @Test
    fun `login should logged in user`() = runTest {
      loginUseCase.invoke()
        assertThat(authGetway.isLoggedIn().first()).isTrue()
    }
}