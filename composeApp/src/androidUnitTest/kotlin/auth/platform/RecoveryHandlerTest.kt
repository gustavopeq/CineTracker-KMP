package auth.platform

import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.After
import org.junit.Test

class RecoveryHandlerTest {

    @After
    fun tearDown() {
        RecoveryHandler.consumeRecoveryToken()
    }

    @Test
    fun `pendingRecoveryToken is initially null`() {
        assertNull(RecoveryHandler.pendingRecoveryToken.value)
    }

    @Test
    fun `handleRecoveryCallback sets pendingRecoveryToken`() {
        RecoveryHandler.handleRecoveryCallback("recovery-token-123")

        assertEquals("recovery-token-123", RecoveryHandler.pendingRecoveryToken.value)
    }

    @Test
    fun `consumeRecoveryToken returns token and clears it`() {
        RecoveryHandler.handleRecoveryCallback("recovery-token-456")

        val token = RecoveryHandler.consumeRecoveryToken()

        assertEquals("recovery-token-456", token)
        assertNull(RecoveryHandler.pendingRecoveryToken.value)
    }

    @Test
    fun `consumeRecoveryToken returns null when no pending token`() {
        val token = RecoveryHandler.consumeRecoveryToken()

        assertNull(token)
    }

    @Test
    fun `consumeRecoveryToken returns null on second call`() {
        RecoveryHandler.handleRecoveryCallback("one-time-token")

        RecoveryHandler.consumeRecoveryToken()
        val secondCall = RecoveryHandler.consumeRecoveryToken()

        assertNull(secondCall)
    }

    @Test
    fun `handleRecoveryCallback overwrites previous token`() {
        RecoveryHandler.handleRecoveryCallback("first-token")
        RecoveryHandler.handleRecoveryCallback("second-token")

        assertEquals("second-token", RecoveryHandler.pendingRecoveryToken.value)
    }
}
