package database.backfill

import common.domain.models.util.MediaType
import common.util.fakeContentEntity
import common.util.fakeMovieResponse
import common.util.fakeShowResponse
import common.util.successFlow
import database.repository.DatabaseRepository
import features.settings.domain.SettingsInteractor
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CachedFieldsBackfillTest {

    private val databaseRepository: DatabaseRepository = mockk()
    private val movieRepository: MovieRepository = mockk()
    private val showRepository: ShowRepository = mockk()
    private val settingsInteractor: SettingsInteractor = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var backfill: CachedFieldsBackfill

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { settingsInteractor.getAppLanguage() } returns "en-US"
        backfill = CachedFieldsBackfill(databaseRepository, movieRepository, showRepository, settingsInteractor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `backfillIfNeeded does nothing when no stale entities`() = runTest {
        coEvery { databaseRepository.getEntitiesWithMissingCachedFields() } returns emptyList()

        backfill.backfillIfNeeded()

        coVerify(exactly = 0) { databaseRepository.updateCachedFields(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `backfillIfNeeded fetches and updates cached fields for movie entity`() = runTest {
        val staleEntity = fakeContentEntity(contentId = 100, mediaType = "MOVIE", posterPath = null)
        coEvery { databaseRepository.getEntitiesWithMissingCachedFields() } returns listOf(staleEntity)
        coEvery { movieRepository.getMovieDetailsById(100, any()) } returns successFlow(
            fakeMovieResponse(id = 100, title = "Test Movie")
        )
        coEvery { databaseRepository.updateCachedFields(any(), any(), any(), any(), any()) } just runs

        backfill.backfillIfNeeded()

        coVerify {
            databaseRepository.updateCachedFields(
                contentId = 100,
                mediaType = MediaType.MOVIE,
                title = "Test Movie",
                posterPath = "/poster.jpg",
                voteAverage = 7.5f
            )
        }
    }

    @Test
    fun `backfillIfNeeded fetches and updates cached fields for show entity`() = runTest {
        val staleEntity = fakeContentEntity(contentId = 200, mediaType = "SHOW", posterPath = null)
        coEvery { databaseRepository.getEntitiesWithMissingCachedFields() } returns listOf(staleEntity)
        coEvery { showRepository.getShowDetailsById(200, any()) } returns successFlow(
            fakeShowResponse(id = 200, name = "Test Show")
        )
        coEvery { databaseRepository.updateCachedFields(any(), any(), any(), any(), any()) } just runs

        backfill.backfillIfNeeded()

        coVerify {
            databaseRepository.updateCachedFields(
                contentId = 200,
                mediaType = MediaType.SHOW,
                title = "Test Show",
                posterPath = "/poster.jpg",
                voteAverage = 7.5f
            )
        }
    }

    @Test
    fun `backfillIfNeeded deduplicates by contentId and mediaType`() = runTest {
        val entity1 = fakeContentEntity(contentId = 100, mediaType = "MOVIE", listId = 1, posterPath = null)
        val entity2 = fakeContentEntity(contentId = 100, mediaType = "MOVIE", listId = 2, posterPath = null)
        coEvery { databaseRepository.getEntitiesWithMissingCachedFields() } returns listOf(entity1, entity2)
        coEvery { movieRepository.getMovieDetailsById(100, any()) } returns successFlow(
            fakeMovieResponse(id = 100)
        )
        coEvery { databaseRepository.updateCachedFields(any(), any(), any(), any(), any()) } just runs

        backfill.backfillIfNeeded()

        coVerify(exactly = 1) { movieRepository.getMovieDetailsById(100, any()) }
        coVerify(exactly = 1) { databaseRepository.updateCachedFields(any(), any(), any(), any(), any()) }
    }
}
