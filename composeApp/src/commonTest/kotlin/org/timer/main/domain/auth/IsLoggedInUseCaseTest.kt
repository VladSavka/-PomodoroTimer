package org.timer.main.breakactivity.org.timer.main.domain.auth
import com.varabyte.truthish.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import org.timer.main.domain.auth.*

class IsLoggedInUseCaseTest {

    private lateinit var isLoggedInUseCase: DefaultGetAuthStateUseCase
    private lateinit var authGateway: FakeAuthGateway

    @BeforeTest
    fun setUp() {
        authGateway = FakeAuthGateway()
        isLoggedInUseCase = DefaultGetAuthStateUseCase(authGateway)
    }

    @Test
    fun `is logged in state should be loading when the user hasn't logged yet`() = runTest {
        val actualResult = isLoggedInUseCase.invoke().first()
        assertThat(actualResult).isEqualTo(AuthState.Loading)
    }

    @Test
    fun `is logged in state should be autentificated when the user has successfully logged in`() = runTest {
        authGateway.login()
        val actualResult = isLoggedInUseCase.invoke().first()
        assertThat(actualResult).isEqualTo(AuthState.Authenticated)
    }

    @Test
    fun `is logged in state should be Not Authenticated when the user has successfully logout`() = runTest {
        authGateway.login()
        authGateway.logout()
        val actualResult = isLoggedInUseCase.invoke().first()
        assertThat(actualResult).isEqualTo(AuthState.NotAuthenticated)
    }
}