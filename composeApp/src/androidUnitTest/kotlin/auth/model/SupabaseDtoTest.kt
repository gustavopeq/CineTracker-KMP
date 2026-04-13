package auth.model

import kotlin.test.assertEquals
import org.junit.Test

class SupabaseDtoTest {

    // region SupabaseUserMetadata.getDisplayName

    @Test
    fun `getDisplayName returns fullName when present`() {
        val metadata = SupabaseUserMetadata(fullName = "John Doe", name = "Johnny")

        assertEquals("John Doe", metadata.getDisplayName())
    }

    @Test
    fun `getDisplayName returns name when fullName is null`() {
        val metadata = SupabaseUserMetadata(fullName = null, name = "Johnny")

        assertEquals("Johnny", metadata.getDisplayName())
    }

    @Test
    fun `getDisplayName returns empty string when both null`() {
        val metadata = SupabaseUserMetadata(fullName = null, name = null)

        assertEquals("", metadata.getDisplayName())
    }

    @Test
    fun `getDisplayName prefers fullName over name`() {
        val metadata = SupabaseUserMetadata(fullName = "Full Name", name = "Short")

        assertEquals("Full Name", metadata.getDisplayName())
    }

    // endregion

    // region SupabaseErrorResponse.getErrorMessage

    @Test
    fun `getErrorMessage returns errorDescription when present`() {
        val error = SupabaseErrorResponse(
            errorDescription = "Description",
            message = "Msg",
            msg = "M",
            error = "E"
        )

        assertEquals("Description", error.getErrorMessage())
    }

    @Test
    fun `getErrorMessage returns message when errorDescription is null`() {
        val error = SupabaseErrorResponse(
            errorDescription = null,
            message = "Message text",
            msg = "M",
            error = "E"
        )

        assertEquals("Message text", error.getErrorMessage())
    }

    @Test
    fun `getErrorMessage returns msg when message and errorDescription null`() {
        val error = SupabaseErrorResponse(
            errorDescription = null,
            message = null,
            msg = "Msg text",
            error = "E"
        )

        assertEquals("Msg text", error.getErrorMessage())
    }

    @Test
    fun `getErrorMessage returns error when all others null`() {
        val error = SupabaseErrorResponse(
            errorDescription = null,
            message = null,
            msg = null,
            error = "Error text"
        )

        assertEquals("Error text", error.getErrorMessage())
    }

    @Test
    fun `getErrorMessage returns Unknown error when all fields null`() {
        val error = SupabaseErrorResponse(
            errorDescription = null,
            message = null,
            msg = null,
            error = null
        )

        assertEquals("Unknown error", error.getErrorMessage())
    }

    // endregion
}
