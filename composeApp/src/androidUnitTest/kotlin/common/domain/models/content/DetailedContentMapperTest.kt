package common.domain.models.content

import common.domain.models.util.MediaType
import common.util.fakeMovieResponse
import common.util.fakeShowResponse
import kotlin.test.assertEquals
import network.models.content.common.CastRoles
import network.models.content.common.ContentCastResponse
import network.models.content.common.PersonResponse
import org.junit.Test

class DetailedContentMapperTest {

    // ── MovieResponse.toDetailedContent ───────────────────────────────────────

    @Test
    fun `MovieResponse toDetailedContent maps all movie-specific fields`() {
        val movie = fakeMovieResponse(id = 1, title = "Inception").copy(
            runtime = 148,
            release_date = "2010-07-16",
            budget = 160000000L,
            revenue = 836800000L
        )

        val result = movie.toDetailedContent()

        assertEquals(1, result.id)
        assertEquals("Inception", result.name)
        assertEquals(MediaType.MOVIE, result.mediaType)
        assertEquals(148, result.runtime)
        assertEquals("2010-07-16", result.releaseDate)
        assertEquals(160000000L, result.budget)
        assertEquals(836800000L, result.revenue)
    }

    // ── ShowResponse.toDetailedContent ────────────────────────────────────────

    @Test
    fun `ShowResponse toDetailedContent maps all show-specific fields`() {
        val show = fakeShowResponse(id = 2, name = "Breaking Bad").copy(
            first_air_date = "2008-01-20",
            last_air_date = "2013-09-29",
            number_of_seasons = 5,
            number_of_episodes = 62
        )

        val result = show.toDetailedContent()

        assertEquals(2, result.id)
        assertEquals("Breaking Bad", result.name)
        assertEquals(MediaType.SHOW, result.mediaType)
        assertEquals("2008-01-20", result.firstAirDate)
        assertEquals("2013-09-29", result.lastAirDate)
        assertEquals(5, result.numberOfSeasons)
        assertEquals(62, result.numberOfEpisodes)
    }

    // ── PersonResponse.toDetailedContent ──────────────────────────────────────

    @Test
    fun `PersonResponse toDetailedContent maps biography to overview`() {
        val person = PersonResponse(
            id = 3,
            name = "Actor",
            biography = "A famous actor",
            birthday = "1990-01-01",
            deathday = null,
            place_of_birth = "Los Angeles"
        )

        val result = person.toDetailedContent()

        assertEquals("A famous actor", result.overview)
        assertEquals(MediaType.PERSON, result.mediaType)
        assertEquals("1990-01-01", result.birthday)
        assertEquals("", result.deathday)
        assertEquals("Los Angeles", result.placeOfBirth)
    }

    // ── Null safety ───────────────────────────────────────────────────────────

    @Test
    fun `toDetailedContent uses orEmpty for null string fields`() {
        val movie = fakeMovieResponse().copy(
            title = null,
            overview = null,
            posterPath = null,
            backdropPath = null,
            release_date = null
        )

        val result = movie.toDetailedContent()

        assertEquals("", result.name)
        assertEquals("", result.overview)
        assertEquals("", result.posterPath)
        assertEquals("", result.backdropPath)
        assertEquals("", result.releaseDate)
    }

    // ── ContentCast.toContentCast ─────────────────────────────────────────────

    @Test
    fun `toContentCast falls back to roles character when character is null`() {
        val response = ContentCastResponse(
            id = 1,
            name = "Actor",
            character = null,
            profilePath = "/pic.jpg",
            roles = listOf(CastRoles(character = "Walter White"))
        )

        val result = response.toContentCast()

        assertEquals("Walter White", result.character)
    }

    @Test
    fun `toContentCast uses character when available`() {
        val response = ContentCastResponse(
            id = 1,
            name = "Actor",
            character = "Jesse Pinkman",
            profilePath = "/pic.jpg"
        )

        val result = response.toContentCast()

        assertEquals("Jesse Pinkman", result.character)
    }

    @Test
    fun `toContentCast returns empty character when both character and roles are null`() {
        val response = ContentCastResponse(
            id = 1,
            name = "Actor",
            character = null,
            roles = null
        )

        val result = response.toContentCast()

        assertEquals("", result.character)
    }
}
