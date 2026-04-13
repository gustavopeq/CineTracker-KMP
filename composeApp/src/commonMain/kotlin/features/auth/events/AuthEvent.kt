package features.auth.events

sealed class AuthEvent {
    data object SignInWithGoogle : AuthEvent()
    data object SignUpWithEmail : AuthEvent()
    data object SignInWithEmail : AuthEvent()
    data object ToggleMode : AuthEvent()
    data object TogglePasswordVisibility : AuthEvent()
    data object ResetPassword : AuthEvent()
    data object DismissError : AuthEvent()
}
