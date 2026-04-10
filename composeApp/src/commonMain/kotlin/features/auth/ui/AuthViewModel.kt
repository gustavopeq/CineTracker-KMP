package features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.repository.AuthRepository
import auth.service.AuthResult
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.auth_error_email_already_registered
import cinetracker_kmp.composeapp.generated.resources.auth_error_email_not_confirmed
import cinetracker_kmp.composeapp.generated.resources.auth_error_generic_sign_in
import cinetracker_kmp.composeapp.generated.resources.auth_error_generic_sign_up
import cinetracker_kmp.composeapp.generated.resources.auth_error_incorrect_credentials
import cinetracker_kmp.composeapp.generated.resources.auth_error_invalid_email
import cinetracker_kmp.composeapp.generated.resources.auth_error_password_too_short
import features.auth.events.AuthEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackbarError = MutableStateFlow<StringResource?>(null)
    val snackbarError: StateFlow<StringResource?> = _snackbarError

    private val _isCreateMode = MutableStateFlow(true)
    val isCreateMode: StateFlow<Boolean> = _isCreateMode

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _formError = MutableStateFlow<StringResource?>(null)
    val formError: StateFlow<StringResource?> = _formError

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess

    fun updateName(value: String) {
        _name.value = value
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            AuthEvent.SignInWithGoogle -> signInWithGoogle()
            AuthEvent.SignUpWithEmail -> signUpWithEmail()
            AuthEvent.SignInWithEmail -> signInWithEmail()
            AuthEvent.ToggleMode -> toggleMode()
            AuthEvent.TogglePasswordVisibility -> togglePasswordVisibility()
            AuthEvent.ResetPassword -> resetPassword()
            AuthEvent.DismissError -> dismissError()
        }
    }

    private fun signInWithGoogle() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.signInWithGoogle()) {
                is AuthResult.Success -> _authSuccess.value = true
                is AuthResult.Error -> _snackbarError.value =
                    Res.string.auth_error_generic_sign_in
            }
            _isLoading.value = false
        }
    }

    private fun signUpWithEmail() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signUpWithEmail(
                email = _email.value,
                password = _password.value,
                name = _name.value
            )
            when (result) {
                is AuthResult.Success -> _authSuccess.value = true
                is AuthResult.Error -> _formError.value = mapSignUpError(result.message)
            }
            _isLoading.value = false
        }
    }

    private fun signInWithEmail() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signInWithEmail(
                email = _email.value,
                password = _password.value
            )
            when (result) {
                is AuthResult.Success -> _authSuccess.value = true
                is AuthResult.Error -> _formError.value = mapSignInError(result.message)
            }
            _isLoading.value = false
        }
    }

    private fun mapSignUpError(apiError: String): StringResource {
        val lower = apiError.lowercase()
        return when {
            "email" in lower && "invalid" in lower -> Res.string.auth_error_invalid_email
            "at least" in lower && "character" in lower -> Res.string.auth_error_password_too_short
            "already registered" in lower -> Res.string.auth_error_email_already_registered
            else -> Res.string.auth_error_generic_sign_up
        }
    }

    private fun mapSignInError(apiError: String): StringResource {
        val lower = apiError.lowercase()
        return when {
            "invalid login credentials" in lower -> Res.string.auth_error_incorrect_credentials
            "email not confirmed" in lower -> Res.string.auth_error_email_not_confirmed
            else -> Res.string.auth_error_generic_sign_in
        }
    }

    private fun toggleMode() {
        _isCreateMode.value = !_isCreateMode.value
        _formError.value = null
    }

    private fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    private fun resetPassword() {
        viewModelScope.launch {
            authRepository.resetPassword(_email.value)
        }
    }

    private fun dismissError() {
        _snackbarError.value = null
        _formError.value = null
    }
}
