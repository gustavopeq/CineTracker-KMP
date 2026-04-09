package auth.platform

import kotlinx.coroutines.CompletableDeferred

object AuthCallbackHandler {
    private var pendingResult: CompletableDeferred<String>? = null

    fun createPendingResult(): CompletableDeferred<String> {
        val deferred = CompletableDeferred<String>()
        pendingResult = deferred
        return deferred
    }

    fun handleCallback(fragment: String) {
        pendingResult?.complete(fragment)
        pendingResult = null
    }

    fun handleError(error: String) {
        pendingResult?.completeExceptionally(Exception(error))
        pendingResult = null
    }
}
