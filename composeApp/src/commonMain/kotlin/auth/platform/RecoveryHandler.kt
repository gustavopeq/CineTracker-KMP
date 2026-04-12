package auth.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RecoveryHandler {

    private val _pendingRecoveryToken = MutableStateFlow<String?>(null)
    val pendingRecoveryToken: StateFlow<String?> = _pendingRecoveryToken.asStateFlow()

    fun handleRecoveryCallback(accessToken: String) {
        _pendingRecoveryToken.value = accessToken
    }

    fun consumeRecoveryToken(): String? {
        val token = _pendingRecoveryToken.value
        _pendingRecoveryToken.value = null
        return token
    }
}
