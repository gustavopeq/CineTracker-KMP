package features.browse.ui.paging

import androidx.paging.PagingSource
import common.domain.models.util.ContentListType
import common.domain.models.util.MediaType
import common.util.errorFlow
import common.util.fakeMoviePagingResponse
import common.util.fakeMovieResponse
import common.util.fakeShowPagingResponse
import common.util.fakeShowResponse
import common.util.successFlow
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class MediaContentPagingSourceTest {

    private val movieRepository: MovieRepository = mockk()
    private val showRepository: ShowRepository = mockk()

    private val refreshParams = PagingSource.LoadParams.Refresh<Int>(
        key = null,
        loadSize = 20,
        placeholdersEnabled = false,
    )
    private val page2Params = PagingSource.LoadParams.Refresh<Int>(
        key = 2,
        loadSize = 20,
        placeholdersEnabled = false,
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun buildSource(mediaType: MediaType) = MediaContentPagingSource(
        movieRepository = movieRepository,
        showRepository = showRepository,
        contentListType = ContentListType.MOVIE_POPULAR,
        mediaType = mediaType,
    )

    // ── MOVIE ─────────────────────────────────────────────────────────────────

    @Test
    fun `load returns Page with mapped items for MOVIE`() = runTest {
        coEvery { movieRepository.getMovieList(any(), any()) } returns successFlow(
            fakeMoviePagingResponse(fakeMovieResponse(id = 1)),
        )

        val result = buildSource(MediaType.MOVIE).load(refreshParams)

        val page = assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
        assertEquals(1, page.data.size)
    }

    @Test
    fun `load returns Error when MOVIE API fails`() = runTest {
        coEvery { movieRepository.getMovieList(any(), any()) } returns errorFlow()

        val result = buildSource(MediaType.MOVIE).load(refreshParams)

        assertIs<PagingSource.LoadResult.Error<Int, *>>(result)
    }

    // ── SHOW ──────────────────────────────────────────────────────────────────

    @Test
    fun `load returns Page with mapped items for SHOW`() = runTest {
        coEvery { showRepository.getShowList(any(), any()) } returns successFlow(
            fakeShowPagingResponse(fakeShowResponse(id = 1)),
        )

        val result = buildSource(MediaType.SHOW).load(refreshParams)

        val page = assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
        assertEquals(1, page.data.size)
    }

    @Test
    fun `load returns Error when SHOW API fails`() = runTest {
        coEvery { showRepository.getShowList(any(), any()) } returns errorFlow()

        val result = buildSource(MediaType.SHOW).load(refreshParams)

        assertIs<PagingSource.LoadResult.Error<Int, *>>(result)
    }

    // ── PERSON (invalid for browse) ───────────────────────────────────────────

    @Test
    fun `load returns Error for PERSON mediaType`() = runTest {
        val result = buildSource(MediaType.PERSON).load(refreshParams)

        assertIs<PagingSource.LoadResult.Error<Int, *>>(result)
    }

    // ── Paging keys ───────────────────────────────────────────────────────────

    @Test
    fun `prevKey is null on first page`() = runTest {
        coEvery { movieRepository.getMovieList(any(), any()) } returns successFlow(
            fakeMoviePagingResponse(fakeMovieResponse()),
        )

        val result = buildSource(MediaType.MOVIE).load(refreshParams)

        val page = assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
        assertNull(page.prevKey)
    }

    @Test
    fun `prevKey and nextKey are correct on page 2`() = runTest {
        coEvery { movieRepository.getMovieList(any(), any()) } returns successFlow(
            fakeMoviePagingResponse(fakeMovieResponse()),
        )

        val result = buildSource(MediaType.MOVIE).load(page2Params)

        val page = assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
    }
}
