package features.home.domain

import common.domain.models.util.MediaType
import common.util.errorFlow
import common.util.fakeContentEntity
import common.util.fakeMoviePagingResponse
import common.util.fakeMovieResponse
import common.util.fakeMultiPagingResponse
import common.util.fakeMultiResponse
import common.util.fakePersonPagingResponse
import common.util.successFlow
import database.repository.DatabaseRepository
import features.details.util.fakePersonResponse
import features.settings.domain.SettingsInteractor
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import network.repository.home.HomeRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

class HomeInteractorTest {

    private val homeRepository: HomeRepository = mockk()
    private val databaseRepository: DatabaseRepository = mockk()
    private val settingsInteractor: SettingsInteractor = mockk()

    private lateinit var interactor: HomeInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { settingsInteractor.getAppLanguage() } returns "en-US"
        every { settingsInteractor.getAppRegion() } returns "US"
        interactor = HomeInteractor(
            homeRepository = homeRepository,
            databaseRepository = databaseRepository,
            settingsInteractor = settingsInteractor
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ── getTrendingMulti ──────────────────────────────────────────────────────

    @Test
    fun `getTrendingMulti returns HomeState with populated trendingList on success`() = runTest {
        coEvery { homeRepository.getTrendingMulti(any()) } returns successFlow(
            fakeMultiPagingResponse(fakeMultiResponse(id = 1))
        )

        val result = interactor.getTrendingMulti()

        assertFalse(result.isFailed())
        assertEquals(1, result.trendingList.value.size)
    }

    @Test
    fun `getTrendingMulti returns error state on API failure`() = runTest {
        coEvery { homeRepository.getTrendingMulti(any()) } returns errorFlow("500")

        val result = interactor.getTrendingMulti()

        assertTrue(result.isFailed())
    }

    @Test
    fun `getTrendingMulti filters out MultiResponse items without poster_path`() = runTest {
        val withPoster = fakeMultiResponse(id = 1)
        val noPoster = fakeMultiResponse(id = 2).copy(posterPath = null)
        coEvery { homeRepository.getTrendingMulti(any()) } returns successFlow(
            fakeMultiPagingResponse(withPoster, noPoster)
        )

        val result = interactor.getTrendingMulti()

        assertEquals(1, result.trendingList.value.size)
    }

    // ── getWatchlistFlow ─────────────────────────────────────────────────────

    @Test
    fun `getWatchlistFlow returns GenericContent list from entity data`() = runTest {
        every { databaseRepository.getAllItemsByListId(any()) } returns flowOf(
            listOf(
                fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name)
            )
        )

        val result = interactor.getWatchlistFlow().first()

        assertEquals(1, result.size)
        assertEquals(MediaType.MOVIE, result[0].mediaType)
    }

    @Test
    fun `getWatchlistFlow includes entities with null posterPath`() = runTest {
        every { databaseRepository.getAllItemsByListId(any()) } returns flowOf(
            listOf(
                fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name),
                fakeContentEntity(contentId = 2, listId = 1, mediaType = MediaType.SHOW.name, posterPath = null)
            )
        )

        val result = interactor.getWatchlistFlow().first()

        assertEquals(2, result.size)
        assertEquals("", result[1].posterPath)
    }

    @Test
    fun `getWatchlistFlow returns empty list when watchlist is empty`() = runTest {
        every { databaseRepository.getAllItemsByListId(any()) } returns flowOf(emptyList())

        val result = interactor.getWatchlistFlow().first()

        assertTrue(result.isEmpty())
    }

    // ── getTrendingPerson ─────────────────────────────────────────────────────

    @Test
    fun `getTrendingPerson returns list of PersonDetails on success`() = runTest {
        coEvery { homeRepository.getTrendingPerson(any()) } returns successFlow(
            fakePersonPagingResponse(fakePersonResponse(id = 1, name = "Actor A"))
        )

        val result = interactor.getTrendingPerson()

        assertEquals(1, result.size)
        assertEquals("Actor A", result[0].title)
    }

    @Test
    fun `getTrendingPerson returns empty list on API error`() = runTest {
        coEvery { homeRepository.getTrendingPerson(any()) } returns errorFlow()

        val result = interactor.getTrendingPerson()

        assertTrue(result.isEmpty())
    }

    // ── getMoviesComingSoon ───────────────────────────────────────────────────

    @Test
    fun `getMoviesComingSoon returns list of GenericContent on success`() = runTest {
        coEvery { homeRepository.getMoviesComingSoon(any(), any(), any(), any()) } returns successFlow(
            fakeMoviePagingResponse(fakeMovieResponse(id = 1))
        )

        val result = interactor.getMoviesComingSoon()

        assertEquals(1, result.size)
    }

    @Test
    fun `getMoviesComingSoon returns empty list on API error`() = runTest {
        coEvery { homeRepository.getMoviesComingSoon(any(), any(), any(), any()) } returns errorFlow()

        val result = interactor.getMoviesComingSoon()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMoviesComingSoon filters out MovieResponse items without poster_path`() = runTest {
        val withPoster = fakeMovieResponse(id = 1)
        val noPoster = fakeMovieResponse(id = 2).copy(posterPath = null)
        coEvery { homeRepository.getMoviesComingSoon(any(), any(), any(), any()) } returns successFlow(
            fakeMoviePagingResponse(withPoster, noPoster)
        )

        val result = interactor.getMoviesComingSoon()

        assertEquals(1, result.size)
    }
}
