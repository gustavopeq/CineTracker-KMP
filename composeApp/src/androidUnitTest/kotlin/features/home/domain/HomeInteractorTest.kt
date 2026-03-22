package features.home.domain

import common.domain.models.util.MediaType
import common.util.errorFlow
import common.util.fakeContentEntity
import common.util.fakeMoviePagingResponse
import common.util.fakeMovieResponse
import common.util.fakeMultiPagingResponse
import common.util.fakeMultiResponse
import common.util.fakePersonPagingResponse
import common.util.fakeShowResponse
import common.util.successFlow
import database.repository.DatabaseRepository
import features.details.util.fakePersonResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import network.repository.home.HomeRepository
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

class HomeInteractorTest {

    private val homeRepository: HomeRepository = mockk()
    private val databaseRepository: DatabaseRepository = mockk()
    private val movieRepository: MovieRepository = mockk()
    private val showRepository: ShowRepository = mockk()

    private lateinit var interactor: HomeInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        interactor = HomeInteractor(
            homeRepository = homeRepository,
            databaseRepository = databaseRepository,
            movieRepository = movieRepository,
            showRepository = showRepository
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ── getTrendingMulti ──────────────────────────────────────────────────────

    @Test
    fun `getTrendingMulti returns HomeState with populated trendingList on success`() = runTest {
        coEvery { homeRepository.getTrendingMulti() } returns successFlow(
            fakeMultiPagingResponse(fakeMultiResponse(id = 1))
        )

        val result = interactor.getTrendingMulti()

        assertFalse(result.isFailed())
        assertEquals(1, result.trendingList.value.size)
    }

    @Test
    fun `getTrendingMulti returns error state on API failure`() = runTest {
        coEvery { homeRepository.getTrendingMulti() } returns errorFlow("500")

        val result = interactor.getTrendingMulti()

        assertTrue(result.isFailed())
    }

    @Test
    fun `getTrendingMulti filters out MultiResponse items without poster_path`() = runTest {
        val withPoster = fakeMultiResponse(id = 1)
        val noPoster = fakeMultiResponse(id = 2).copy(poster_path = null)
        coEvery { homeRepository.getTrendingMulti() } returns successFlow(
            fakeMultiPagingResponse(withPoster, noPoster)
        )

        val result = interactor.getTrendingMulti()

        assertEquals(1, result.trendingList.value.size)
    }

    // ── getAllWatchlist ────────────────────────────────────────────────────────

    @Test
    fun `getAllWatchlist returns GenericContent list for MOVIE entities`() = runTest {
        coEvery { databaseRepository.getAllItemsByListId(any()) } returns listOf(
            fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name)
        )
        coEvery { movieRepository.getMovieDetailsById(1) } returns successFlow(fakeMovieResponse(id = 1))

        val result = interactor.getAllWatchlist()

        assertEquals(1, result.size)
        assertEquals(MediaType.MOVIE, result[0].mediaType)
    }

    @Test
    fun `getAllWatchlist returns GenericContent list for SHOW entities`() = runTest {
        coEvery { databaseRepository.getAllItemsByListId(any()) } returns listOf(
            fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.SHOW.name)
        )
        coEvery { showRepository.getShowDetailsById(1) } returns successFlow(fakeShowResponse(id = 1))

        val result = interactor.getAllWatchlist()

        assertEquals(1, result.size)
        assertEquals(MediaType.SHOW, result[0].mediaType)
    }

    @Test
    fun `getAllWatchlist skips PERSON entities`() = runTest {
        coEvery { databaseRepository.getAllItemsByListId(any()) } returns listOf(
            fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.PERSON.name)
        )

        val result = interactor.getAllWatchlist()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllWatchlist returns empty list when watchlist is empty`() = runTest {
        coEvery { databaseRepository.getAllItemsByListId(any()) } returns emptyList()

        val result = interactor.getAllWatchlist()

        assertTrue(result.isEmpty())
    }

    // ── getTrendingPerson ─────────────────────────────────────────────────────

    @Test
    fun `getTrendingPerson returns list of PersonDetails on success`() = runTest {
        coEvery { homeRepository.getTrendingPerson() } returns successFlow(
            fakePersonPagingResponse(fakePersonResponse(id = 1, name = "Actor A"))
        )

        val result = interactor.getTrendingPerson()

        assertEquals(1, result.size)
        assertEquals("Actor A", result[0].title)
    }

    @Test
    fun `getTrendingPerson returns empty list on API error`() = runTest {
        coEvery { homeRepository.getTrendingPerson() } returns errorFlow()

        val result = interactor.getTrendingPerson()

        assertTrue(result.isEmpty())
    }

    // ── getMoviesComingSoon ───────────────────────────────────────────────────

    @Test
    fun `getMoviesComingSoon returns list of GenericContent on success`() = runTest {
        coEvery { homeRepository.getMoviesComingSoon(any(), any()) } returns successFlow(
            fakeMoviePagingResponse(fakeMovieResponse(id = 1))
        )

        val result = interactor.getMoviesComingSoon()

        assertEquals(1, result.size)
    }

    @Test
    fun `getMoviesComingSoon returns empty list on API error`() = runTest {
        coEvery { homeRepository.getMoviesComingSoon(any(), any()) } returns errorFlow()

        val result = interactor.getMoviesComingSoon()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMoviesComingSoon filters out MovieResponse items without poster_path`() = runTest {
        val withPoster = fakeMovieResponse(id = 1)
        val noPoster = fakeMovieResponse(id = 2).copy(poster_path = null)
        coEvery { homeRepository.getMoviesComingSoon(any(), any()) } returns successFlow(
            fakeMoviePagingResponse(withPoster, noPoster)
        )

        val result = interactor.getMoviesComingSoon()

        assertEquals(1, result.size)
    }
}
