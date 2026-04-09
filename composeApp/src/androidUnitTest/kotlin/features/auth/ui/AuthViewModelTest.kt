package features.auth.ui

import auth.model.AuthState
import auth.repository.AuthRepository
import auth.service.AuthResult
import features.auth.events.AuthEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.delay
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
            AuthResult.Error("Invalid credentials")
        val viewModel = createViewModel()
        viewModel.onEvent(AuthEvent.ToggleMode)
        viewModel.onEvent(AuthEvent.SignInWithEmail)
        advanceUntilIdle()
        assertTrue(viewModel.formError.value.isNotEmpty())

        viewModel.onEvent(AuthEvent.ToggleMode)

        assertTrue(viewModel.formError.value.isEmpty())
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
        assertEquals("Unable to sign in. Please try again.", viewModel.snackbarError.value)
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
    fun `SignUpWithEmail sets formError on failure`() = runTest {
        coEvery { authRepository.signUpWithEmail(any(), any(), any()) } returns
            AuthResult.Error("Email already in use")
        val viewModel = createViewModel()
        viewModel.updateName("John")
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("password123")

        viewModel.onEvent(AuthEvent.SignUpWithEmail)
        advanceUntilIdle()

        assertEquals("Unable to create account. Please try again.", viewModel.formError.value)
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
    fun `SignInWithEmail sets formError on failure`() = runTest {
        coEvery { authRepository.signInWithEmail(any(), any()) } returns
            AuthResult.Error("Invalid credentials")
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("wrong")

        viewModel.onEvent(AuthEvent.SignInWithEmail)
        advanceUntilIdle()

        assertEquals("Unable to sign in. Please try again.", viewModel.formError.value)
        assertFalse(viewModel.authSuccess.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `ResetPassword calls repository resetPassword with current email`() = runTest {
        coEvery { authRepository.resetPassword(any()) } returns AuthResult.Success(Unit)
        val viewModel = createViewModel()
        viewModel.updateEmail("john@example.com")

        viewModel.onEvent(AuthEvent.ResetPassword)
        advanceUntilIdle()

        coVerify { authRepository.resetPassword("john@example.com") }
    }

    @Test
    fun `DismissError clears both error fields`() = runTest {
        coEvery { authRepository.signInWithGoogle() } returns
            AuthResult.Error("Google sign-in failed")
        coEvery { authRepository.signInWithEmail(any(), any()) } returns
            AuthResult.Error("Invalid credentials")
        val viewModel = createViewModel()

        viewModel.onEvent(AuthEvent.SignInWithGoogle)
        advanceUntilIdle()
        assertTrue(viewModel.snackbarError.value.isNotEmpty())

        viewModel.onEvent(AuthEvent.ToggleMode)
        viewModel.onEvent(AuthEvent.SignInWithEmail)
        advanceUntilIdle()
        assertTrue(viewModel.formError.value.isNotEmpty())

        viewModel.onEvent(AuthEvent.DismissError)

        assertTrue(viewModel.snackbarError.value.isEmpty())
        assertTrue(viewModel.formError.value.isEmpty())
    }
}
