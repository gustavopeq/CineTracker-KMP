package features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.repository.AuthRepository
import auth.service.AuthResult
import features.auth.events.AuthEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackbarError = MutableStateFlow("")
    val snackbarError: StateFlow<String> = _snackbarError

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

    private val _formError = MutableStateFlow("")
    val formError: StateFlow<String> = _formError

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
                is AuthResult.Error -> _snackbarError.value = GENERIC_SIGN_IN_ERROR
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
                is AuthResult.Error -> _formError.value = GENERIC_SIGN_UP_ERROR
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
                is AuthResult.Error -> _formError.value = GENERIC_SIGN_IN_ERROR
            }
            _isLoading.value = false
        }
    }

    companion object {
        private const val GENERIC_SIGN_IN_ERROR = "Unable to sign in. Please try again."
        private const val GENERIC_SIGN_UP_ERROR = "Unable to create account. Please try again."
    }

    private fun toggleMode() {
        _isCreateMode.value = !_isCreateMode.value
        _formError.value = ""
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
        _snackbarError.value = ""
        _formError.value = ""
    }
}
