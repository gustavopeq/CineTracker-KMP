package features.details.domain

import common.domain.models.util.MediaType
import common.util.errorFlow
import common.util.fakeMoviePagingResponse
import common.util.fakeMovieResponse
import common.util.fakeShowResponse
import common.util.successFlow
import core.LanguageManager
import database.repository.DatabaseRepository
import database.repository.PersonalRatingRepository
import features.details.util.fakeCastResponse
import features.details.util.fakeContentCastResponse
import features.details.util.fakeContentCreditsResponse
import features.details.util.fakeContentCrewResponse
import features.details.util.fakePersonCreditsResponse
import features.details.util.fakePersonImagesResponse
import features.details.util.fakePersonResponse
import features.details.util.fakeProviderResponse
import features.details.util.fakeVideosByIdResponse
import features.details.util.fakeWatchProvidersResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import network.models.content.common.CountryProviderResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.person.PersonImagesResponse
import network.repository.movie.MovieRepository
import network.repository.person.PersonRepository
import network.repository.show.ShowRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

class DetailsInteractorTest {

    private val movieRepository: MovieRepository = mockk()
    private val showRepository: ShowRepository = mockk()
    private val personRepository: PersonRepository = mockk()
    private val listInteractor: features.watchlist.domain.ListInteractor = mockk()
    private val personalRatingRepository: PersonalRatingRepository = mockk()
    private val databaseRepository: DatabaseRepository = mockk()

    private lateinit var interactor: DetailsInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(LanguageManager)
        every { LanguageManager.getUserCountryCode() } returns "US"

        interactor = DetailsInteractor(
            movieRepository = movieRepository,
            showRepository = showRepository,
            personRepository = personRepository,
            listInteractor = listInteractor,
            personalRatingRepository = personalRatingRepository,
            databaseRepository = databaseRepository
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ── getContentDetailsById ─────────────────────────────────────────────────

    @Test
    fun `getContentDetailsById returns movie details when mediaType is MOVIE`() = runTest {
        coEvery { movieRepository.getMovieDetailsById(1) } returns successFlow(fakeMovieResponse(id = 1))
        coEvery { movieRepository.getStreamingProviders(1) } returns successFlow(
            WatchProvidersResponse(id = 1, results = emptyMap())
        )

        val result = interactor.getContentDetailsById(1, MediaType.MOVIE)

        assertFalse(result.isFailed())
        assertNotNull(result.detailsInfo.value)
        assertEquals(MediaType.MOVIE, result.detailsInfo.value?.mediaType)
    }

    @Test
    fun `getContentDetailsById returns show details when mediaType is SHOW`() = runTest {
        coEvery { showRepository.getShowDetailsById(1) } returns successFlow(fakeShowResponse(id = 1))
        coEvery { showRepository.getStreamingProviders(1) } returns successFlow(
            WatchProvidersResponse(id = 1, results = emptyMap())
        )

        val result = interactor.getContentDetailsById(1, MediaType.SHOW)

        assertFalse(result.isFailed())
        assertNotNull(result.detailsInfo.value)
        assertEquals(MediaType.SHOW, result.detailsInfo.value?.mediaType)
    }

    @Test
    fun `getContentDetailsById returns person details when mediaType is PERSON`() = runTest {
        coEvery { personRepository.getPersonDetailsById(1) } returns successFlow(fakePersonResponse(id = 1))

        val result = interactor.getContentDetailsById(1, MediaType.PERSON)

        assertFalse(result.isFailed())
        assertNotNull(result.detailsInfo.value)
        assertEquals(MediaType.PERSON, result.detailsInfo.value?.mediaType)
    }

    @Test
    fun `getContentDetailsById sets error state on API error`() = runTest {
        coEvery { movieRepository.getMovieDetailsById(1) } returns errorFlow("500")
        coEvery { movieRepository.getStreamingProviders(any()) } returns successFlow(
            WatchProvidersResponse(id = 1, results = emptyMap())
        )

        val result = interactor.getContentDetailsById(1, MediaType.MOVIE)

        assertTrue(result.isFailed())
        assertEquals("500", result.errorCode)
    }

    @Test
    fun `getContentDetailsById returns empty state for UNKNOWN mediaType without calling any repo`() = runTest {
        val result = interactor.getContentDetailsById(1, MediaType.UNKNOWN)

        assertFalse(result.isFailed())
        assertNull(result.detailsInfo.value)
        verify(exactly = 0) { movieRepository.hashCode() } // no repo interaction
        coVerify(exactly = 0) { movieRepository.getMovieDetailsById(any()) }
        coVerify(exactly = 0) { showRepository.getShowDetailsById(any()) }
        coVerify(exactly = 0) { personRepository.getPersonDetailsById(any()) }
    }

    @Test
    fun `getContentDetailsById injects streaming providers when country code matches`() = runTest {
        coEvery { movieRepository.getMovieDetailsById(1) } returns successFlow(fakeMovieResponse())
        coEvery { movieRepository.getStreamingProviders(1) } returns successFlow(
            fakeWatchProvidersResponse(countryCode = "US")
        )

        val result = interactor.getContentDetailsById(1, MediaType.MOVIE)

        assertTrue(result.detailsInfo.value?.streamProviders?.isNotEmpty() == true)
    }

    @Test
    fun `getContentDetailsById leaves streamProviders empty when country not in results`() = runTest {
        coEvery { movieRepository.getMovieDetailsById(1) } returns successFlow(fakeMovieResponse())
        coEvery { movieRepository.getStreamingProviders(1) } returns successFlow(
            fakeWatchProvidersResponse(countryCode = "BR")
        )

        val result = interactor.getContentDetailsById(1, MediaType.MOVIE)

        assertTrue(result.detailsInfo.value?.streamProviders.isNullOrEmpty())
    }

    // ── getContentCastById ────────────────────────────────────────────────────

    @Test
    fun `getContentCastById filters out cast with empty profile_path`() = runTest {
        coEvery { movieRepository.getMovieCreditsById(1) } returns successFlow(
            fakeContentCreditsResponse(
                fakeContentCastResponse(id = 1, name = "Actor A", profilePath = "/profile.jpg"),
                fakeContentCastResponse(id = 2, name = "Actor B", profilePath = ""),
                fakeContentCastResponse(id = 3, name = "Actor C", profilePath = null)
            )
        )

        val result = interactor.getContentCastById(1, MediaType.MOVIE)

        assertEquals(1, result.detailsCast.value.size)
        assertEquals("Actor A", result.detailsCast.value[0].name)
    }

    @Test
    fun `getContentCastById sorts remaining cast by order ascending`() = runTest {
        coEvery { movieRepository.getMovieCreditsById(1) } returns successFlow(
            fakeContentCreditsResponse(
                fakeContentCastResponse(id = 1, name = "Third", profilePath = "/p.jpg", order = 5),
                fakeContentCastResponse(id = 2, name = "First", profilePath = "/p.jpg", order = 1),
                fakeContentCastResponse(id = 3, name = "Second", profilePath = "/p.jpg", order = 3)
            )
        )

        val result = interactor.getContentCastById(1, MediaType.MOVIE)
        val names = result.detailsCast.value.map { it.name }

        assertEquals(listOf("First", "Second", "Third"), names)
    }

    @Test
    fun `getContentCastById places null-order entries last`() = runTest {
        coEvery { movieRepository.getMovieCreditsById(1) } returns successFlow(
            fakeContentCreditsResponse(
                fakeContentCastResponse(id = 1, name = "NoOrder", profilePath = "/p.jpg", order = null),
                fakeContentCastResponse(id = 2, name = "Second", profilePath = "/p.jpg", order = 2),
                fakeContentCastResponse(id = 3, name = "First", profilePath = "/p.jpg", order = 0)
            )
        )

        val result = interactor.getContentCastById(1, MediaType.MOVIE)
        val names = result.detailsCast.value.map { it.name }

        assertEquals(listOf("First", "Second", "NoOrder"), names)
    }

    @Test
    fun `getContentCastById sets error state on API error`() = runTest {
        coEvery { movieRepository.getMovieCreditsById(1) } returns errorFlow("503")

        val result = interactor.getContentCastById(1, MediaType.MOVIE)

        assertTrue(result.isFailed())
    }

    @Test
    fun `getContentCastById returns empty cast for PERSON mediaType without calling any repo`() = runTest {
        val result = interactor.getContentCastById(1, MediaType.PERSON)

        assertFalse(result.isFailed())
        assertTrue(result.detailsCast.value.isEmpty())
        coVerify(exactly = 0) { movieRepository.getMovieCreditsById(any()) }
        coVerify(exactly = 0) { showRepository.getShowCreditsById(any()) }
    }

    @Test
    fun `getContentCastById extracts director names from crew for movies`() = runTest {
        coEvery { movieRepository.getMovieCreditsById(1) } returns successFlow(
            fakeContentCreditsResponse(
                fakeContentCastResponse(id = 1, name = "Actor", profilePath = "/p.jpg"),
                crew = listOf(
                    fakeContentCrewResponse(id = 10, name = "Steven Spielberg", job = "Director"),
                    fakeContentCrewResponse(id = 11, name = "John Williams", job = "Composer"),
                    fakeContentCrewResponse(id = 12, name = "Janusz Kaminski", job = "Director of Photography")
                )
            )
        )

        val result = interactor.getContentCastById(1, MediaType.MOVIE)

        assertEquals(listOf("Steven Spielberg"), result.directorNames.value)
    }

    @Test
    fun `getContentCastById extracts multiple director names`() = runTest {
        coEvery { movieRepository.getMovieCreditsById(1) } returns successFlow(
            fakeContentCreditsResponse(
                crew = listOf(
                    fakeContentCrewResponse(id = 10, name = "Lana Wachowski", job = "Director"),
                    fakeContentCrewResponse(id = 11, name = "Lilly Wachowski", job = "Director")
                )
            )
        )

        val result = interactor.getContentCastById(1, MediaType.MOVIE)

        assertEquals(listOf("Lana Wachowski", "Lilly Wachowski"), result.directorNames.value)
    }

    @Test
    fun `getContentCastById returns empty director names when no Director in crew`() = runTest {
        coEvery { movieRepository.getMovieCreditsById(1) } returns successFlow(
            fakeContentCreditsResponse(
                crew = listOf(
                    fakeContentCrewResponse(id = 10, name = "John Williams", job = "Composer")
                )
            )
        )

        val result = interactor.getContentCastById(1, MediaType.MOVIE)

        assertTrue(result.directorNames.value.isEmpty())
    }

    @Test
    fun `getContentCastById does not extract director names for SHOW`() = runTest {
        coEvery { showRepository.getShowCreditsById(1) } returns successFlow(
            fakeContentCreditsResponse(
                crew = listOf(
                    fakeContentCrewResponse(id = 10, name = "Someone", job = "Director")
                )
            )
        )

        val result = interactor.getContentCastById(1, MediaType.SHOW)

        assertTrue(result.directorNames.value.isEmpty())
    }

    // ── getContentVideosById ──────────────────────────────────────────────────

    @Test
    fun `getContentVideosById returns video list for MOVIE`() = runTest {
        coEvery { movieRepository.getMovieVideosById(1) } returns successFlow(fakeVideosByIdResponse(count = 2))

        val result = interactor.getContentVideosById(1, MediaType.MOVIE)

        assertEquals(2, result.size)
    }

    @Test
    fun `getContentVideosById returns video list for SHOW`() = runTest {
        coEvery { showRepository.getShowVideosById(1) } returns successFlow(fakeVideosByIdResponse(count = 3))

        val result = interactor.getContentVideosById(1, MediaType.SHOW)

        assertEquals(3, result.size)
    }

    @Test
    fun `getContentVideosById returns empty list on API error`() = runTest {
        coEvery { movieRepository.getMovieVideosById(1) } returns errorFlow()

        val result = interactor.getContentVideosById(1, MediaType.MOVIE)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getContentVideosById returns empty list for PERSON mediaType`() = runTest {
        val result = interactor.getContentVideosById(1, MediaType.PERSON)

        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { movieRepository.getMovieVideosById(any()) }
    }

    // ── getRecommendationsContentById ─────────────────────────────────────────

    @Test
    fun `getRecommendationsContentById returns recommendations when non-empty`() = runTest {
        val movie = fakeMovieResponse(id = 1, title = "Rec Movie")
        coEvery { movieRepository.getRecommendationsMoviesById(1) } returns successFlow(
            fakeMoviePagingResponse(movie)
        )

        val result = interactor.getRecommendationsContentById(1, MediaType.MOVIE)

        assertEquals(1, result.size)
        coVerify(exactly = 0) { movieRepository.getSimilarMoviesById(any()) }
    }

    @Test
    fun `getRecommendationsContentById falls back to similar when recommendations empty`() = runTest {
        val similar = fakeMovieResponse(id = 2, title = "Similar Movie")
        coEvery { movieRepository.getRecommendationsMoviesById(1) } returns successFlow(
            fakeMoviePagingResponse()
        )
        coEvery { movieRepository.getSimilarMoviesById(1) } returns successFlow(
            fakeMoviePagingResponse(similar)
        )

        val result = interactor.getRecommendationsContentById(1, MediaType.MOVIE)

        assertEquals(1, result.size)
        coVerify(exactly = 1) { movieRepository.getSimilarMoviesById(1) }
    }

    @Test
    fun `getRecommendationsContentById returns empty list when both recommendations and similar are empty`() = runTest {
        coEvery { movieRepository.getRecommendationsMoviesById(1) } returns successFlow(
            fakeMoviePagingResponse()
        )
        coEvery { movieRepository.getSimilarMoviesById(1) } returns successFlow(
            fakeMoviePagingResponse()
        )

        val result = interactor.getRecommendationsContentById(1, MediaType.MOVIE)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getRecommendationsContentById filters out items without poster_path`() = runTest {
        val withPoster = fakeMovieResponse(id = 1, title = "Has Poster")
        val noPoster = fakeMovieResponse(id = 2, title = "No Poster").copy(posterPath = null)
        coEvery { movieRepository.getRecommendationsMoviesById(1) } returns successFlow(
            fakeMoviePagingResponse(withPoster, noPoster)
        )

        val result = interactor.getRecommendationsContentById(1, MediaType.MOVIE)

        assertEquals(1, result.size)
        assertEquals("Has Poster", result[0].name)
    }

    @Test
    fun `getRecommendationsContentById filters out items without title`() = runTest {
        val withTitle = fakeMovieResponse(id = 1, title = "Good Title")
        val noTitle = fakeMovieResponse(id = 2).copy(title = null)
        coEvery { movieRepository.getRecommendationsMoviesById(1) } returns successFlow(
            fakeMoviePagingResponse(withTitle, noTitle)
        )

        val result = interactor.getRecommendationsContentById(1, MediaType.MOVIE)

        assertEquals(1, result.size)
    }

    // ── getStreamingProviders ─────────────────────────────────────────────────

    @Test
    fun `getStreamingProviders returns providers for matching country code`() = runTest {
        coEvery { movieRepository.getStreamingProviders(1) } returns successFlow(
            fakeWatchProvidersResponse(countryCode = "US", providers = listOf(fakeProviderResponse("Netflix")))
        )

        val result = interactor.getStreamingProviders(1, MediaType.MOVIE)

        assertEquals(1, result.size)
        assertEquals("Netflix", result[0].providerName)
    }

    @Test
    fun `getStreamingProviders returns empty list when country not in results`() = runTest {
        coEvery { movieRepository.getStreamingProviders(1) } returns successFlow(
            fakeWatchProvidersResponse(countryCode = "BR")
        )

        val result = interactor.getStreamingProviders(1, MediaType.MOVIE)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getStreamingProviders returns empty list for PERSON mediaType`() = runTest {
        val result = interactor.getStreamingProviders(1, MediaType.PERSON)

        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { movieRepository.getStreamingProviders(any()) }
    }

    @Test
    fun `getStreamingProviders returns empty list when flatrate is null`() = runTest {
        coEvery { movieRepository.getStreamingProviders(1) } returns successFlow(
            WatchProvidersResponse(id = 1, results = mapOf("US" to CountryProviderResponse(flatrate = null)))
        )

        val result = interactor.getStreamingProviders(1, MediaType.MOVIE)

        assertTrue(result.isEmpty())
    }

    // ── getPersonCreditsById ──────────────────────────────────────────────────

    @Test
    fun `getPersonCreditsById filters out entries with empty name`() = runTest {
        // GenericContent.name = castResponse.title = (_title ?: name). Set title=null, name="" to get empty name.
        coEvery { personRepository.getPersonCreditsById(1) } returns successFlow(
            fakePersonCreditsResponse(
                fakeCastResponse(id = 1, name = "Valid Name", posterPath = "/p.jpg", title = "Valid Title"),
                fakeCastResponse(id = 2, name = "", posterPath = "/p.jpg", title = null)
            )
        )

        val result = interactor.getPersonCreditsById(1)

        assertEquals(1, result.size)
        assertEquals("Valid Title", result[0].name)
    }

    @Test
    fun `getPersonCreditsById filters out entries with empty posterPath`() = runTest {
        coEvery { personRepository.getPersonCreditsById(1) } returns successFlow(
            fakePersonCreditsResponse(
                fakeCastResponse(id = 1, name = "Actor A", posterPath = "/p.jpg"),
                fakeCastResponse(id = 2, name = "Actor B", posterPath = null)
            )
        )

        val result = interactor.getPersonCreditsById(1)

        assertEquals(1, result.size)
    }

    @Test
    fun `getPersonCreditsById returns entries passing all filters`() = runTest {
        coEvery { personRepository.getPersonCreditsById(1) } returns successFlow(
            fakePersonCreditsResponse(
                fakeCastResponse(id = 1, name = "Actor A", posterPath = "/p.jpg"),
                fakeCastResponse(id = 2, name = "Actor B", posterPath = "/p2.jpg")
            )
        )

        val result = interactor.getPersonCreditsById(1)

        assertEquals(2, result.size)
    }

    @Test
    fun `getPersonCreditsById returns empty list on error`() = runTest {
        coEvery { personRepository.getPersonCreditsById(1) } returns errorFlow()

        val result = interactor.getPersonCreditsById(1)

        assertTrue(result.isEmpty())
    }

    // ── getPersonImages ───────────────────────────────────────────────────────

    @Test
    fun `getPersonImages returns images with non-empty file_path`() = runTest {
        coEvery { personRepository.getPersonImagesById(1) } returns successFlow(
            fakePersonImagesResponse("/img1.jpg", "/img2.jpg", null, "")
        )

        val result = interactor.getPersonImages(1)

        assertEquals(2, result.size)
    }

    @Test
    fun `getPersonImages returns empty list when profiles is null`() = runTest {
        coEvery { personRepository.getPersonImagesById(1) } returns successFlow(
            PersonImagesResponse(id = 1, profiles = null)
        )

        val result = interactor.getPersonImages(1)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getPersonImages returns empty list on API error`() = runTest {
        coEvery { personRepository.getPersonImagesById(1) } returns errorFlow()

        val result = interactor.getPersonImages(1)

        assertTrue(result.isEmpty())
    }

    // ── verifyContentInLists ──────────────────────────────────────────────────

    @Test
    fun `verifyContentInLists delegates to listInteractor`() = runTest {
        val expected = mapOf(1 to true, 2 to false)
        every { listInteractor.verifyContentInLists(42, MediaType.MOVIE) } returns flowOf(expected)

        val result = interactor.verifyContentInLists(42, MediaType.MOVIE).first()

        assertEquals(expected, result)
        verify { listInteractor.verifyContentInLists(42, MediaType.MOVIE) }
    }

    // ── toggleWatchlist ───────────────────────────────────────────────────────

    @Test
    fun `toggleWatchlist delegates to listInteractor`() = runTest {
        coEvery { listInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any()) } returns Unit

        interactor.toggleWatchlist(
            currentStatus = false,
            contentId = 1,
            mediaType = MediaType.MOVIE,
            listId = 1
        )

        coVerify { listInteractor.toggleWatchlist(false, 1, MediaType.MOVIE, 1, any(), any(), any()) }
    }

    // ── getAllLists ───────────────────────────────────────────────────────────

    @Test
    fun `getAllLists delegates to listInteractor`() = runTest {
        val expected = listOf(
            common.domain.models.list.ListItem(id = 1, name = "Watchlist"),
            common.domain.models.list.ListItem(id = 2, name = "Watched")
        )
        every { listInteractor.getAllLists() } returns flowOf(expected)

        val result = interactor.getAllLists().first()

        assertEquals(expected, result)
        verify { listInteractor.getAllLists() }
    }

    // ── getPersonalRating ─────────────────────────────────────────────────────

    @Test
    fun `getPersonalRating returns float value when rating exists`() = runTest {
        every { personalRatingRepository.getRating(42) } returns flowOf(7.5f)

        val result = interactor.getPersonalRating(42).first()

        assertEquals(7.5f, result)
    }

    @Test
    fun `getPersonalRating returns null when no rating exists`() = runTest {
        every { personalRatingRepository.getRating(42) } returns flowOf(null)

        val result = interactor.getPersonalRating(42).first()

        assertNull(result)
    }
}
