package auth.model

sealed interface AuthState {
    data object LoggedOut : AuthState
    data class LoggedIn(val userId: String, val displayName: String) : AuthState
}
