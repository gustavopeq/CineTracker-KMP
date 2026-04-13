package auth.platform

import auth.model.SignInResult

expect class PlatformSignInProvider {
    suspend fun signInWithGoogle(): SignInResult
    suspend fun signInWithApple(): SignInResult
}
