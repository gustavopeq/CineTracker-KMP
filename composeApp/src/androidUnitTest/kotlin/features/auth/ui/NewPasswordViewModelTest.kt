package features.auth.ui

import auth.platform.RecoveryHandler
import auth.repository.AuthRepository
import auth.service.AuthResult
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.auth_error_password_too_short
import cinetracker_kmp.composeapp.generated.resources.auth_error_passwords_dont_match
import cinetracker_kmp.composeapp.generated.resources.auth_error_update_password
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk(relaxUnitFun = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        RecoveryHandler.handleRecoveryCallback("test-recovery-token")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = NewPasswordViewModel(authRepository)

    // region initial state

    @Test
    fun `initial state has empty password fields`() {
        val viewModel = createViewModel()

        assertEquals("", viewModel.password.value)
        assertEquals("", viewModel.confirmPassword.value)
    }

    @Test
    fun `initial state has passwords hidden`() {
        val viewModel = createViewModel()

        assertFalse(viewModel.isPasswordVisible.value)
        assertFalse(viewModel.isConfirmPasswordVisible.value)
    }

    @Test
    fun `initial state has no error`() {
        val viewModel = createViewModel()

        assertNull(viewModel.error.value)
        assertFalse(viewModel.isSuccess.value)
        assertFalse(viewModel.isLoading.value)
    }

    // endregion

    // region field updates

    @Test
    fun `updatePassword updates password state`() {
        val viewModel = createViewModel()

        viewModel.updatePassword("newpass123")

        assertEquals("newpass123", viewModel.password.value)
    }

    @Test
    fun `updateConfirmPassword updates confirmPassword state`() {
        val viewModel = createViewModel()

        viewModel.updateConfirmPassword("newpass123")

        assertEquals("newpass123", viewModel.confirmPassword.value)
    }

    @Test
    fun `togglePasswordVisibility toggles visibility`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.isPasswordVisible.value)

        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.isPasswordVisible.value)

        viewModel.togglePasswordVisibility()
        assertFalse(viewModel.isPasswordVisible.value)
    }

    @Test
    fun `toggleConfirmPasswordVisibility toggles visibility`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.isConfirmPasswordVisible.value)

        viewModel.toggleConfirmPasswordVisibility()
        assertTrue(viewModel.isConfirmPasswordVisible.value)

        viewModel.toggleConfirmPasswordVisibility()
        assertFalse(viewModel.isConfirmPasswordVisible.value)
    }

    // endregion

    // region validation

    @Test
    fun `submit with short password sets password too short error`() {
        val viewModel = createViewModel()
        viewModel.updatePassword("12345")
        viewModel.updateConfirmPassword("12345")

        viewModel.submit()

        assertEquals(Res.string.auth_error_password_too_short, viewModel.error.value)
        assertFalse(viewModel.isSuccess.value)
    }

    @Test
    fun `submit with mismatched passwords sets passwords dont match error`() {
        val viewModel = createViewModel()
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("different456")

        viewModel.submit()

        assertEquals(Res.string.auth_error_passwords_dont_match, viewModel.error.value)
        assertFalse(viewModel.isSuccess.value)
    }

    @Test
    fun `submit clears previous error before validating`() {
        val viewModel = createViewModel()
        viewModel.updatePassword("12345")
        viewModel.updateConfirmPassword("12345")
        viewModel.submit()
        assertEquals(Res.string.auth_error_password_too_short, viewModel.error.value)

        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("different456")
        viewModel.submit()

        assertEquals(Res.string.auth_error_passwords_dont_match, viewModel.error.value)
    }

    // endregion

    // region submit success

    @Test
    fun `submit with valid matching passwords calls updatePassword`() = runTest {
        coEvery {
            authRepository.updatePassword("test-recovery-token", "password123")
        } returns AuthResult.Success(Unit)
        val viewModel = createViewModel()
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        viewModel.submit()
        advanceUntilIdle()

        coVerify { authRepository.updatePassword("test-recovery-token", "password123") }
        assertTrue(viewModel.isSuccess.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `submit sets loading during API call`() = runTest {
        coEvery {
            authRepository.updatePassword(any(), any())
        } coAnswers {
            delay(1000)
            AuthResult.Success(Unit)
        }
        val viewModel = createViewModel()
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        viewModel.submit()
        runCurrent()
        assertTrue(viewModel.isLoading.value)

        advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    // endregion

    // region submit error

    @Test
    fun `submit with API error sets update password error`() = runTest {
        coEvery {
            authRepository.updatePassword(any(), any())
        } returns AuthResult.Error("Server error")
        val viewModel = createViewModel()
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        viewModel.submit()
        advanceUntilIdle()

        assertEquals(Res.string.auth_error_update_password, viewModel.error.value)
        assertFalse(viewModel.isSuccess.value)
    }

    // endregion

    // region missing recovery token

    @Test
    fun `submit without recovery token does not call API`() = runTest {
        RecoveryHandler.consumeRecoveryToken()
        val viewModel = createViewModel()
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")

        viewModel.submit()
        advanceUntilIdle()

        coVerify(exactly = 0) { authRepository.updatePassword(any(), any()) }
        assertFalse(viewModel.isSuccess.value)
    }

    // endregion
}
