package common.domain.models.content

import common.domain.models.util.MediaType
import common.util.fakeMovieResponse
import common.util.fakeMultiResponse
import common.util.fakeShowResponse
import network.models.content.common.PersonResponse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GenericContentMapperTest {

    // ── toGenericContent — type detection ──────────────────────────────────────

    @Test
    fun `toGenericContent returns GenericContent for MovieResponse`() {
        val result = fakeMovieResponse(id = 1, title = "Movie").toGenericContent()

        assertNotNull(result)
        assertEquals(1, result.id)
        assertEquals("Movie", result.name)
        assertEquals(MediaType.MOVIE, result.mediaType)
    }

    @Test
    fun `toGenericContent returns GenericContent for ShowResponse`() {
        val result = fakeShowResponse(id = 2, name = "Show").toGenericContent()

        assertNotNull(result)
        assertEquals(2, result.id)
        assertEquals("Show", result.name)
        assertEquals(MediaType.SHOW, result.mediaType)
    }

    @Test
    fun `toGenericContent returns GenericContent for PersonResponse with profile_path`() {
        val person = PersonResponse(id = 3, name = "Actor", profile_path = "/actor.jpg")

        val result = person.toGenericContent()

        assertNotNull(result)
        assertEquals(3, result.id)
        assertEquals("Actor", result.name)
        assertEquals(MediaType.PERSON, result.mediaType)
    }

    @Test
    fun `toGenericContent maps MultiResponse using media_type field`() {
        val result = fakeMultiResponse(id = 4, mediaType = "tv").toGenericContent()

        assertNotNull(result)
        assertEquals(MediaType.SHOW, result.mediaType)
    }

    // ── toGenericContent — filtering ──────────────────────────────────────────

    @Test
    fun `toGenericContent returns null for UNKNOWN media type`() {
        val multi = fakeMultiResponse(id = 5, mediaType = "invalid_type")

        val result = multi.toGenericContent()

        assertNull(result)
    }

    @Test
    fun `toGenericContent returns null when poster_path is null`() {
        val movie = fakeMovieResponse(id = 6).copy(poster_path = null)

        val result = movie.toGenericContent()

        assertNull(result)
    }
}
