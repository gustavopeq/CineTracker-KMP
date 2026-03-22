package features.search.ui.paging

import androidx.paging.PagingSource
import common.domain.models.util.MediaType
import common.util.errorFlow
import common.util.fakeMoviePagingResponse
import common.util.fakeMovieResponse
import common.util.fakeMultiPagingResponse
import common.util.fakePersonPagingResponse
import common.util.fakeShowPagingResponse
import common.util.fakeShowResponse
import common.util.successFlow
import features.details.util.fakePersonResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import network.repository.search.SearchRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

class SearchPagingSourceTest {

    private val searchRepository: SearchRepository = mockk()

    private val refreshParams = PagingSource.LoadParams.Refresh<Int>(
        key = null,
        loadSize = 20,
        placeholdersEnabled = false
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun buildSource(query: String = "test", mediaType: MediaType? = null) = SearchPagingSource(
        searchRepository = searchRepository,
        query = query,
        mediaType = mediaType
    )

    // ── mediaType routing ─────────────────────────────────────────────────────

    @Test
    fun `load calls onSearchMovieByQuery for MOVIE mediaType`() = runTest {
        coEvery { searchRepository.onSearchMovieByQuery(any(), any()) } returns successFlow(
            fakeMoviePagingResponse(fakeMovieResponse(id = 1))
        )

        val result = buildSource(mediaType = MediaType.MOVIE).load(refreshParams)

        val page = assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
        assertEquals(1, page.data.size)
    }

    @Test
    fun `load calls onSearchShowByQuery for SHOW mediaType`() = runTest {
        coEvery { searchRepository.onSearchShowByQuery(any(), any()) } returns successFlow(
            fakeShowPagingResponse(fakeShowResponse(id = 1))
        )

        val result = buildSource(mediaType = MediaType.SHOW).load(refreshParams)

        val page = assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
        assertEquals(1, page.data.size)
    }

    @Test
    fun `load calls onSearchPersonByQuery for PERSON mediaType`() = runTest {
        coEvery { searchRepository.onSearchPersonByQuery(any(), any()) } returns successFlow(
            fakePersonPagingResponse(fakePersonResponse(id = 1, name = "Actor"))
        )

        val result = buildSource(mediaType = MediaType.PERSON).load(refreshParams)

        assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
    }

    @Test
    fun `load calls onSearchMultiByQuery for null mediaType`() = runTest {
        coEvery { searchRepository.onSearchMultiByQuery(any(), any()) } returns successFlow(
            fakeMultiPagingResponse()
        )

        val result = buildSource(mediaType = null).load(refreshParams)

        assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
    }

    // ── Error handling ────────────────────────────────────────────────────────

    @Test
    fun `load returns Error on API failure`() = runTest {
        coEvery { searchRepository.onSearchMovieByQuery(any(), any()) } returns errorFlow()

        val result = buildSource(mediaType = MediaType.MOVIE).load(refreshParams)

        assertIs<PagingSource.LoadResult.Error<Int, *>>(result)
    }

    // ── Paging keys ───────────────────────────────────────────────────────────

    @Test
    fun `prevKey is null on page 1 and nextKey is 2`() = runTest {
        coEvery { searchRepository.onSearchMovieByQuery(any(), any()) } returns successFlow(
            fakeMoviePagingResponse(fakeMovieResponse())
        )

        val result = buildSource(mediaType = MediaType.MOVIE).load(refreshParams)

        val page = assertIs<PagingSource.LoadResult.Page<Int, *>>(result)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }
}
