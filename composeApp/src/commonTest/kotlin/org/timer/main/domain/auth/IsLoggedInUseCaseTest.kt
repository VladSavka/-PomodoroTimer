package org.timer.main.breakactivity.org.timer.main.domain.auth
import com.varabyte.truthish.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import org.timer.main.domain.auth.*

class IsLoggedInUseCaseTest {

    private lateinit var isLoggedInUseCase: DefaultIsLoggedInUseCase
    private lateinit var authGateway: FakeAuthGateway

    @BeforeTest
    fun setUp() {
        authGateway = FakeAuthGateway()
        isLoggedInUseCase = DefaultIsLoggedInUseCase(authGateway)
    }

    @Test
    fun `is logged in state should be false when the user hasn't logged yet`() = runTest {
        val actualResult = isLoggedInUseCase.invoke().first()
        assertThat(actualResult).isFalse()
    }

    @Test
    fun `is logged in state should be true when the user has successfully logged in`() = runTest {
        authGateway.login()
        val actualResult = isLoggedInUseCase.invoke().first()
        assertThat(actualResult).isTrue()
    }
}