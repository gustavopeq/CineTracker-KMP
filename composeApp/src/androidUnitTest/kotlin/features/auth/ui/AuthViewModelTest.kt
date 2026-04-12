package features.auth.ui

import auth.repository.AuthRepository
import auth.service.AuthResult
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.auth_error_email_already_registered
import cinetracker_kmp.composeapp.generated.resources.auth_error_generic_sign_in
import cinetracker_kmp.composeapp.generated.resources.auth_error_generic_sign_up
import cinetracker_kmp.composeapp.generated.resources.auth_error_incorrect_credentials
import cinetracker_kmp.composeapp.generated.resources.auth_error_invalid_email
import cinetracker_kmp.composeapp.generated.resources.auth_error_password_too_short
import cinetracker_kmp.composeapp.generated.resources.auth_error_reset_password
import features.auth.events.AuthEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertIs
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import auth.model.AuthState
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk(relaxUnitFun = true)
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.LoggedOut)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        every { authRepository.authState } returns authStateFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = AuthViewModel(authRepository)

    @Test
    fun `initial state has isCreateMode true`() {
        val viewModel = createViewModel()

        assertTrue(viewModel.isCreateMode.value)
    }

    @Test
    fun `ToggleMode switches from create to sign in mode`() {
        val viewModel = createViewModel()
        assertTrue(viewModel.isCreateMode.value)

        viewModel.onEvent(AuthEvent.ToggleMode)

        assertFalse(viewModel.isCreateMode.value)
    }

    @Test
    fun `ToggleMode switches back to create mode`() {
        val viewModel = createViewModel()

        viewModel.onEvent(AuthEvent.ToggleMode)
        assertFalse(viewModel.isCreateMode.value)

        viewModel.onEvent(AuthEvent.ToggleMode)
        assertTrue(viewModel.isCreateMode.value)
    }

    @Test
    fun `ToggleMode clears form error`() = runTest {
        coEvery { authRepository.signInWithEmail(any(), any()) } returns
            AuthResult.Error("Invalid login credentials")
        val viewModel = createViewModel()
        viewModel.onEvent(AuthEvent.ToggleMode)
        viewModel.onEvent(AuthEvent.SignInWithEmail)
        advanceUntilIdle()
        assertNotNull(viewModel.formError.value)

        viewModel.onEvent(AuthEvent.ToggleMode)

        assertNull(viewModel.formError.value)
    }

    @Test
    fun `TogglePasswordVisibility toggles visibility`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.isPasswordVisible.value)

        viewModel.onEvent(AuthEvent.TogglePasswordVisibility)

        assertTrue(viewModel.isPasswordVisible.value)

        viewModel.onEvent(AuthEvent.TogglePasswordVisibility)

        assertFalse(viewModel.isPasswordVisible.value)
    }

    @Test
    fun `SignInWithGoogle shows loading while in progress`() = runTest {
        coEvery { authRepository.signInWithGoogle() } coAnswers {
            delay(1000)
            AuthResult.Success(Unit)
        }
        val viewModel = createViewModel()

        viewModel.onEvent(AuthEvent.SignInWithGoogle)
        runCurrent()
        assertTrue(viewModel.isLoading.value)

        advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `SignInWithGoogle calls repository and emits authSuccess`() = runTest {
        coEvery { authRepository.signInWithGoogle() } returns AuthResult.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onEvent(AuthEvent.SignInWithGoogle)
        advanceUntilIdle()

        coVerify { authRepository.signInWithGoogle() }
        assertTrue(viewModel.authSuccess.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `SignInWithGoogle sets snackbarError on failure`() = runTest {
        coEvery { authRepository.signInWithGoogle() } returns
            AuthResult.Error("Google sign-in failed")
        val viewModel = createViewModel()

        viewModel.onEvent(AuthEvent.SignInWithGoogle)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals(Res.string.auth_error_generic_sign_in, viewModel.snackbarError.value)
        assertFalse(viewModel.authSuccess.value)
    }

    @Test
    fun `SignUpWithEmail calls repository with form field values and emits authSuccess`() = runTest {
        coEvery { authRepository.signUpWithEmail(any(), any(), any()) } returns
            AuthResult.Success(Unit)
        val viewModel = createViewModel()
        viewModel.updateName("John")
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("password123")

        viewModel.onEvent(AuthEvent.SignUpWithEmail)
        advanceUntilIdle()

        coVerify { authRepository.signUpWithEmail("john@example.com", "password123", "John") }
        assertTrue(viewModel.authSuccess.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `SignUpWithEmail sets formError for invalid email`() = runTest {
        coEvery { authRepository.signUpWithEmail(any(), any(), any()) } returns
            AuthResult.Error("Unable to validate email address: invalid format")
        val viewModel = createViewModel()
        viewModel.updateName("John")
        viewModel.updateEmail("bad-email")
        viewModel.updatePassword("password123")

        viewModel.onEvent(AuthEvent.SignUpWithEmail)
        advanceUntilIdle()

        assertEquals(Res.string.auth_error_invalid_email, viewModel.formError.value)
        assertFalse(viewModel.authSuccess.value)
    }

    @Test
    fun `SignUpWithEmail sets formError for short password`() = runTest {
        coEvery { authRepository.signUpWithEmail(any(), any(), any()) } returns
            AuthResult.Error("Password should be at least 6 characters")
        val viewModel = createViewModel()
        viewModel.updateName("John")
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("123")

        viewModel.onEvent(AuthEvent.SignUpWithEmail)
        advanceUntilIdle()

        assertEquals(Res.string.auth_error_password_too_short, viewModel.formError.value)
        assertFalse(viewModel.authSuccess.value)
    }

    @Test
    fun `SignUpWithEmail sets formError for already registered email`() = runTest {
        coEvery { authRepository.signUpWithEmail(any(), any(), any()) } returns
            AuthResult.Error("User already registered")
        val viewModel = createViewModel()
        viewModel.updateName("John")
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("password123")

        viewModel.onEvent(AuthEvent.SignUpWithEmail)
        advanceUntilIdle()

        assertEquals(Res.string.auth_error_email_already_registered, viewModel.formError.value)
        assertFalse(viewModel.authSuccess.value)
    }

    @Test
    fun `SignUpWithEmail sets generic formError for unknown error`() = runTest {
        coEvery { authRepository.signUpWithEmail(any(), any(), any()) } returns
            AuthResult.Error("Some unexpected error")
        val viewModel = createViewModel()
        viewModel.updateName("John")
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("password123")

        viewModel.onEvent(AuthEvent.SignUpWithEmail)
        advanceUntilIdle()

        assertEquals(Res.string.auth_error_generic_sign_up, viewModel.formError.value)
        assertFalse(viewModel.authSuccess.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `SignInWithEmail calls repository and emits authSuccess`() = runTest {
        coEvery { authRepository.signInWithEmail(any(), any()) } returns
            AuthResult.Success(Unit)
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("password123")

        viewModel.onEvent(AuthEvent.SignInWithEmail)
        advanceUntilIdle()

        coVerify { authRepository.signInWithEmail("john@example.com", "password123") }
        assertTrue(viewModel.authSuccess.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `SignInWithEmail sets formError for incorrect credentials`() = runTest {
        coEvery { authRepository.signInWithEmail(any(), any()) } returns
            AuthResult.Error("Invalid login credentials")
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("wrong")

        viewModel.onEvent(AuthEvent.SignInWithEmail)
        advanceUntilIdle()

        assertEquals(Res.string.auth_error_incorrect_credentials, viewModel.formError.value)
        assertFalse(viewModel.authSuccess.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `SignInWithEmail sets generic formError for unknown error`() = runTest {
        coEvery { authRepository.signInWithEmail(any(), any()) } returns
            AuthResult.Error("Some unexpected error")
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("wrong")

        viewModel.onEvent(AuthEvent.SignInWithEmail)
        advanceUntilIdle()

        assertEquals(Res.string.auth_error_generic_sign_in, viewModel.formError.value)
        assertFalse(viewModel.authSuccess.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `ResetPassword calls repository and emits Success`() = runTest {
        coEvery { authRepository.resetPassword(any()) } returns AuthResult.Success(Unit)
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")

        viewModel.onEvent(AuthEvent.ResetPassword)
        advanceUntilIdle()

        coVerify { authRepository.resetPassword("john@example.com") }
        assertIs<ResetPasswordState.Success>(viewModel.resetPasswordState.value)
    }

    @Test
    fun `ResetPassword shows Loading while in progress`() = runTest {
        coEvery { authRepository.resetPassword(any()) } coAnswers {
            delay(1000)
            AuthResult.Success(Unit)
        }
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")

        viewModel.onEvent(AuthEvent.ResetPassword)
        runCurrent()
        assertIs<ResetPasswordState.Loading>(viewModel.resetPasswordState.value)

        advanceUntilIdle()
        assertIs<ResetPasswordState.Success>(viewModel.resetPasswordState.value)
    }

    @Test
    fun `ResetPassword emits Error on API failure`() = runTest {
        coEvery { authRepository.resetPassword(any()) } returns
            AuthResult.Error("Rate limit exceeded")
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")

        viewModel.onEvent(AuthEvent.ResetPassword)
        advanceUntilIdle()

        val state = viewModel.resetPasswordState.value
        assertIs<ResetPasswordState.Error>(state)
        assertEquals(Res.string.auth_error_reset_password, state.message)
    }

    @Test
    fun `ResetPassword emits Error for blank email without calling repository`() = runTest {
        val viewModel = createViewModel()
        viewModel.updateEmail("")

        viewModel.onEvent(AuthEvent.ResetPassword)
        advanceUntilIdle()

        val state = viewModel.resetPasswordState.value
        assertIs<ResetPasswordState.Error>(state)
        assertEquals(Res.string.auth_error_invalid_email, state.message)
        coVerify(exactly = 0) { authRepository.resetPassword(any()) }
    }

    @Test
    fun `ResetPassword emits Error for email without at sign`() = runTest {
        val viewModel = createViewModel()
        viewModel.updateEmail("notanemail")

        viewModel.onEvent(AuthEvent.ResetPassword)
        advanceUntilIdle()

        val state = viewModel.resetPasswordState.value
        assertIs<ResetPasswordState.Error>(state)
        assertEquals(Res.string.auth_error_invalid_email, state.message)
        coVerify(exactly = 0) { authRepository.resetPassword(any()) }
    }

    @Test
    fun `clearResetPasswordState resets to Idle`() = runTest {
        coEvery { authRepository.resetPassword(any()) } returns AuthResult.Success(Unit)
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")
        viewModel.onEvent(AuthEvent.ResetPassword)
        advanceUntilIdle()
        assertIs<ResetPasswordState.Success>(viewModel.resetPasswordState.value)

        viewModel.clearResetPasswordState()

        assertIs<ResetPasswordState.Idle>(viewModel.resetPasswordState.value)
    }

    @Test
    fun `DismissError clears both error fields`() = runTest {
        coEvery { authRepository.signInWithGoogle() } returns
            AuthResult.Error("Google sign-in failed")
        coEvery { authRepository.signInWithEmail(any(), any()) } returns
            AuthResult.Error("Invalid login credentials")
        val viewModel = createViewModel()

        viewModel.onEvent(AuthEvent.SignInWithGoogle)
        advanceUntilIdle()
        assertNotNull(viewModel.snackbarError.value)

        viewModel.onEvent(AuthEvent.ToggleMode)
        viewModel.onEvent(AuthEvent.SignInWithEmail)
        advanceUntilIdle()
        assertNotNull(viewModel.formError.value)

        viewModel.onEvent(AuthEvent.DismissError)

        assertNull(viewModel.snackbarError.value)
        assertNull(viewModel.formError.value)
    }
}
