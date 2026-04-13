package features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.platform.RecoveryHandler
import auth.repository.AuthRepository
import auth.service.AuthResult
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.auth_error_password_too_short
import cinetracker_kmp.composeapp.generated.resources.auth_error_passwords_dont_match
import cinetracker_kmp.composeapp.generated.resources.auth_error_update_password
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class NewPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val accessToken: String? = RecoveryHandler.consumeRecoveryToken()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _isConfirmPasswordVisible = MutableStateFlow(false)
    val isConfirmPasswordVisible: StateFlow<Boolean> = _isConfirmPasswordVisible

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<StringResource?>(null)
    val error: StateFlow<StringResource?> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _isConfirmPasswordVisible.value = !_isConfirmPasswordVisible.value
    }

    fun submit() {
        _error.value = null
        val currentPassword = _password.value
        val currentConfirmPassword = _confirmPassword.value

        when {
            currentPassword.length < MIN_PASSWORD_LENGTH -> {
                _error.value = Res.string.auth_error_password_too_short
            }
            currentPassword != currentConfirmPassword -> {
                _error.value = Res.string.auth_error_passwords_dont_match
            }
            else -> {
                val token = accessToken ?: return
                viewModelScope.launch {
                    _isLoading.value = true
                    when (authRepository.updatePassword(token, currentPassword)) {
                        is AuthResult.Success -> _isSuccess.value = true
                        is AuthResult.Error -> _error.value =
                            Res.string.auth_error_update_password
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    companion object {
        internal const val MIN_PASSWORD_LENGTH = 6
    }
}
