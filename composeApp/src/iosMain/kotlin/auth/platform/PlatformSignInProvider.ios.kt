package auth.platform

import auth.model.SignInResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.AuthenticationServices.ASPresentationAnchor
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class PlatformSignInProvider {

    private var activeAuthController: ASAuthorizationController? = null
    private var activeDelegate: NSObject? = null

    actual suspend fun signInWithApple(): SignInResult = suspendCancellableCoroutine { cont ->
        val provider = ASAuthorizationAppleIDProvider()
        val request = provider.createRequest().apply {
            requestedScopes = listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail)
        }

        val delegate = object :
            NSObject(),
            ASAuthorizationControllerDelegateProtocol,
            ASAuthorizationControllerPresentationContextProvidingProtocol {

            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithAuthorization: ASAuthorization
            ) {
                activeAuthController = null
                activeDelegate = null
                val credential =
                    didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
                val tokenData = credential?.identityToken
                if (tokenData != null) {
                    val idToken = NSString.create(
                        data = tokenData,
                        encoding = NSUTF8StringEncoding
                    )?.toString()
                    if (idToken != null) {
                        cont.resume(SignInResult.IdToken(token = idToken, provider = "apple"))
                    } else {
                        cont.resumeWithException(Exception("Failed to decode Apple ID token"))
                    }
                } else {
                    cont.resumeWithException(Exception("No identity token from Apple"))
                }
            }

            override fun authorizationController(controller: ASAuthorizationController, didCompleteWithError: NSError) {
                activeAuthController = null
                activeDelegate = null
                cont.resumeWithException(Exception(didCompleteWithError.localizedDescription))
            }

            override fun presentationAnchorForAuthorizationController(
                controller: ASAuthorizationController
            ): ASPresentationAnchor = getKeyWindow()
        }

        activeDelegate = delegate

        val controller = ASAuthorizationController(
            authorizationRequests = listOf(request)
        )
        controller.delegate = delegate
        controller.presentationContextProvider = delegate
        activeAuthController = controller
        controller.performRequests()
    }

    actual suspend fun signInWithGoogle(): SignInResult = suspendCancellableCoroutine { cont ->
        val handler = GoogleSignInBridge.onSignInRequest
        if (handler == null) {
            cont.resumeWithException(Exception("Google Sign-In not configured"))
            return@suspendCancellableCoroutine
        }
        GoogleSignInBridge.onSignInResult = { idToken, nonce, error ->
            if (error != null) {
                cont.resumeWithException(Exception(error))
            } else if (idToken != null) {
                cont.resume(
                    SignInResult.IdToken(
                        token = idToken,
                        provider = "google",
                        nonce = nonce
                    )
                )
            } else {
                cont.resumeWithException(Exception("No ID token returned from Google"))
            }
        }
        handler()
    }

    private fun getKeyWindow(): UIWindow {
        val scene = UIApplication.sharedApplication.connectedScenes
            .firstOrNull { it is UIWindowScene } as? UIWindowScene
        return scene?.keyWindow ?: UIWindow()
    }
}
