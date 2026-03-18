package common.ui

import common.domain.models.util.MediaType
import common.domain.models.util.SortTypeItem
import database.repository.DatabaseRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val databaseRepository: DatabaseRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = MainViewModel(databaseRepository)

    // ── updateSortType ────────────────────────────────────────────────────────

    @Test
    fun `updateSortType sets movieSortType when media type is MOVIE`() {
        val viewModel = createViewModel()
        // default is MOVIE
        viewModel.updateSortType(SortTypeItem.TopRated)

        assertIs<SortTypeItem.TopRated>(viewModel.movieSortType.value)
    }

    @Test
    fun `updateSortType sets showSortType when media type is SHOW`() {
        val viewModel = createViewModel()
        viewModel.updateMediaType(MediaType.SHOW)
        viewModel.updateSortType(SortTypeItem.ShowTopRated)

        assertIs<SortTypeItem.ShowTopRated>(viewModel.showSortType.value)
    }

    @Test
    fun `updateSortType does nothing for PERSON media type`() {
        val viewModel = createViewModel()
        viewModel.updateMediaType(MediaType.PERSON)
        viewModel.updateSortType(SortTypeItem.Popular)

        // should remain at defaults
        assertIs<SortTypeItem.NowPlaying>(viewModel.movieSortType.value)
        assertIs<SortTypeItem.AiringToday>(viewModel.showSortType.value)
    }

    // ── createNewList ─────────────────────────────────────────────────────────

    @Test
    fun `createNewList calls closeSheet when list created`() = runTest {
        coEvery { databaseRepository.addNewList(any()) } returns true
        val viewModel = createViewModel()
        var sheetClosed = false

        viewModel.createNewList { sheetClosed = true }

        assertTrue(sheetClosed)
        assertFalse(viewModel.isDuplicatedListName.value)
    }

    @Test
    fun `createNewList sets isDuplicatedListName when list not created`() = runTest {
        coEvery { databaseRepository.addNewList(any()) } returns false
        val viewModel = createViewModel()
        var sheetClosed = false

        viewModel.createNewList { sheetClosed = true }

        assertFalse(sheetClosed)
        assertTrue(viewModel.isDuplicatedListName.value)
    }

    // ── updateDisplayCreateNewList ────────────────────────────────────────────

    @Test
    fun `updateDisplayCreateNewList resets text field and duplicate flag`() {
        val viewModel = createViewModel()
        viewModel.updateCreateNewListTextField("something")

        viewModel.updateDisplayCreateNewList(true)

        assertEquals("", viewModel.newListTextFieldValue.value)
        assertFalse(viewModel.isDuplicatedListName.value)
        assertTrue(viewModel.displayCreateNewList.value)
    }

    // ── updateCreateNewListTextField ──────────────────────────────────────────

    @Test
    fun `updateCreateNewListTextField clears duplicate flag when true`() = runTest {
        coEvery { databaseRepository.addNewList(any()) } returns false
        val viewModel = createViewModel()
        viewModel.createNewList {} // sets isDuplicatedListName = true

        assertTrue(viewModel.isDuplicatedListName.value)

        viewModel.updateCreateNewListTextField("new name")

        assertFalse(viewModel.isDuplicatedListName.value)
        assertEquals("new name", viewModel.newListTextFieldValue.value)
    }
}
