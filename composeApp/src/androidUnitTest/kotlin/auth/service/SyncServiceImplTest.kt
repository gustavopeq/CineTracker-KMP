package auth.service

import auth.model.CloudContentDownload
import auth.model.CloudListDownload
import auth.model.CloudRatingDownload
import auth.model.UploadSnapshotRequest
import auth.platform.TokenStorage
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.dao.PersonalRatingDao
import database.model.ContentEntity
import database.model.ListEntity
import database.model.PersonalRatingEntity
import database.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.junit.After
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncServiceImplTest {

    private val authService: SupabaseAuthService = mockk()
    private val tokenStorage: TokenStorage = mockk()
    private val contentEntityDao: ContentEntityDao = mockk()
    private val listEntityDao: ListEntityDao = mockk()
    private val personalRatingDao: PersonalRatingDao = mockk()
    private val settingsRepository: SettingsRepository = mockk(relaxUnitFun = true)
    private val testScope = TestScope()

    private lateinit var syncService: SyncServiceImpl

    @Before
    fun setUp() {
        syncService = SyncServiceImpl(
            authService = authService,
            tokenStorage = tokenStorage,
            contentEntityDao = contentEntityDao,
            listEntityDao = listEntityDao,
            personalRatingDao = personalRatingDao,
            settingsRepository = settingsRepository,
            scope = testScope
        )
    }

    // region performUpload

    @Test
    fun `performUpload reads all DAOs and calls uploadSnapshot`() = runTest {
        val lists = listOf(ListEntity(listId = 1, listName = "watchlist", isDefault = true))
        val content = listOf(ContentEntity(contentId = 100, mediaType = "MOVIE", listId = 1, createdAt = 1000L))
        val ratings = listOf(PersonalRatingEntity(contentId = 100, mediaType = "MOVIE", rating = 8.5f))

        coEvery { listEntityDao.getAllSnapshot() } returns lists
        coEvery { contentEntityDao.getAllSnapshot() } returns content
        coEvery { personalRatingDao.getAllSnapshot() } returns ratings
        coEvery { authService.uploadSnapshot(any(), any()) } returns AuthResult.Success(Unit)

        val result = syncService.performUpload("test-token")

        assertIs<AuthResult.Success<Unit>>(result)
        val requestSlot = slot<UploadSnapshotRequest>()
        coVerify { authService.uploadSnapshot("test-token", capture(requestSlot)) }
        assertEquals(1, requestSlot.captured.lists.size)
        assertEquals(1, requestSlot.captured.content.size)
        assertEquals(1, requestSlot.captured.ratings.size)
        assertEquals(1, requestSlot.captured.lists[0].localListId)
        assertEquals(100, requestSlot.captured.content[0].contentId)
        assertEquals(8.5f, requestSlot.captured.ratings[0].rating)
    }

    @Test
    fun `performUpload returns error on service failure`() = runTest {
        coEvery { listEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { contentEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { personalRatingDao.getAllSnapshot() } returns emptyList()
        coEvery { authService.uploadSnapshot(any(), any()) } returns AuthResult.Error("Network error")

        val result = syncService.performUpload("test-token")

        assertIs<AuthResult.Error>(result)
        assertEquals("Network error", result.message)
    }

    // endregion

    // region performDownload

    @Test
    fun `performDownload clears local data and inserts cloud data`() = runTest {
        val cloudLists = listOf(
            CloudListDownload(id = "uuid-1", localListId = 1, listName = "watchlist", isDefault = true)
        )
        val cloudContent = listOf(
            CloudContentDownload(
                contentId = 100, mediaType = "MOVIE", listId = "uuid-1",
                createdAt = 1000L, title = "Movie", posterPath = "/poster.jpg", voteAverage = 7.5f
            )
        )
        val cloudRatings = listOf(
            CloudRatingDownload(contentId = 100, mediaType = "MOVIE", rating = 8.0f)
        )

        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Success(cloudLists)
        coEvery { authService.fetchCloudContent("token", "user-1") } returns AuthResult.Success(cloudContent)
        coEvery { authService.fetchCloudRatings("token", "user-1") } returns AuthResult.Success(cloudRatings)
        coEvery { listEntityDao.deleteAll() } just runs
        coEvery { personalRatingDao.deleteAll() } just runs
        coEvery { listEntityDao.insertAll(any()) } just runs
        coEvery { contentEntityDao.insertAll(any()) } just runs
        coEvery { personalRatingDao.insertAll(any()) } just runs

        val result = syncService.performDownload("token", "user-1")

        assertIs<AuthResult.Success<Unit>>(result)
        coVerifyOrder {
            listEntityDao.deleteAll()
            personalRatingDao.deleteAll()
            listEntityDao.insertAll(any())
            contentEntityDao.insertAll(any())
            personalRatingDao.insertAll(any())
        }
        coVerify { listEntityDao.insertAll(match { it.size == 1 && it[0].listId == 1 }) }
        coVerify { contentEntityDao.insertAll(match { it.size == 1 && it[0].contentId == 100 && it[0].listId == 1 }) }
        coVerify { personalRatingDao.insertAll(match { it.size == 1 && it[0].rating == 8.0f }) }
    }

    @Test
    fun `performDownload skips content with unmapped list IDs`() = runTest {
        val cloudLists = listOf(
            CloudListDownload(id = "uuid-1", localListId = 1, listName = "watchlist", isDefault = true)
        )
        val cloudContent = listOf(
            CloudContentDownload(
                contentId = 100, mediaType = "MOVIE", listId = "uuid-unknown",
                createdAt = 1000L, title = "Orphan"
            )
        )

        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Success(cloudLists)
        coEvery { authService.fetchCloudContent("token", "user-1") } returns AuthResult.Success(cloudContent)
        coEvery { authService.fetchCloudRatings("token", "user-1") } returns AuthResult.Success(emptyList())
        coEvery { listEntityDao.deleteAll() } just runs
        coEvery { personalRatingDao.deleteAll() } just runs
        coEvery { listEntityDao.insertAll(any()) } just runs
        coEvery { contentEntityDao.insertAll(any()) } just runs
        coEvery { personalRatingDao.insertAll(any()) } just runs

        syncService.performDownload("token", "user-1")

        coVerify { contentEntityDao.insertAll(match { it.isEmpty() }) }
    }

    @Test
    fun `performDownload returns error when fetch lists fails`() = runTest {
        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Error("Failed")

        val result = syncService.performDownload("token", "user-1")

        assertIs<AuthResult.Error>(result)
    }

    // endregion

    // region hasCloudData

    @Test
    fun `hasCloudData returns true when cloud lists exist`() = runTest {
        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Success(
            listOf(CloudListDownload(id = "uuid-1", localListId = 1, listName = "watchlist", isDefault = true))
        )

        assertTrue(syncService.hasCloudData("token", "user-1"))
    }

    @Test
    fun `hasCloudData returns false when no cloud lists`() = runTest {
        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Success(emptyList())

        assertFalse(syncService.hasCloudData("token", "user-1"))
    }

    @Test
    fun `hasCloudData returns false on error`() = runTest {
        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Error("Failed")

        assertFalse(syncService.hasCloudData("token", "user-1"))
    }

    // endregion

    // region requestUpload

    @Test
    fun `requestUpload sets hasLocalChanges and triggers upload`() = testScope.runTest {
        every { tokenStorage.getAccessToken() } returns "test-token"
        coEvery { listEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { contentEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { personalRatingDao.getAllSnapshot() } returns emptyList()
        coEvery { authService.uploadSnapshot(any(), any()) } returns AuthResult.Success(Unit)

        syncService.requestUpload()
        advanceUntilIdle()

        coVerify { settingsRepository.setHasLocalChanges(true) }
        coVerify { authService.uploadSnapshot("test-token", any()) }
        coVerify { settingsRepository.setHasLocalChanges(false) }
    }

    @Test
    fun `requestUpload skips when no access token`() = testScope.runTest {
        every { tokenStorage.getAccessToken() } returns null

        syncService.requestUpload()
        advanceUntilIdle()

        coVerify { settingsRepository.setHasLocalChanges(true) }
        coVerify(exactly = 0) { authService.uploadSnapshot(any(), any()) }
    }

    @Test
    fun `requestUpload does not clear flag on failure`() = testScope.runTest {
        every { tokenStorage.getAccessToken() } returns "test-token"
        coEvery { listEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { contentEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { personalRatingDao.getAllSnapshot() } returns emptyList()
        coEvery { authService.uploadSnapshot(any(), any()) } returns AuthResult.Error("Failed")

        syncService.requestUpload()
        advanceUntilIdle()

        coVerify { settingsRepository.setHasLocalChanges(true) }
        coVerify(exactly = 0) { settingsRepository.setHasLocalChanges(false) }
    }

    @Test
    fun `requestUpload debounces multiple rapid calls`() = testScope.runTest {
        every { tokenStorage.getAccessToken() } returns "test-token"
        coEvery { listEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { contentEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { personalRatingDao.getAllSnapshot() } returns emptyList()
        coEvery { authService.uploadSnapshot(any(), any()) } returns AuthResult.Success(Unit)

        syncService.requestUpload()
        syncService.requestUpload()
        syncService.requestUpload()
        advanceUntilIdle()

        coVerify(exactly = 1) { authService.uploadSnapshot(any(), any()) }
    }

    // endregion

    // region performDownload additional

    @Test
    fun `performDownload returns error when fetch content fails`() = runTest {
        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Success(
            listOf(CloudListDownload(id = "uuid-1", localListId = 1, listName = "watchlist", isDefault = true))
        )
        coEvery { authService.fetchCloudContent("token", "user-1") } returns AuthResult.Error("Content failed")

        val result = syncService.performDownload("token", "user-1")

        assertIs<AuthResult.Error>(result)
        assertEquals("Content failed", (result as AuthResult.Error).message)
    }

    @Test
    fun `performDownload returns error when fetch ratings fails`() = runTest {
        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Success(
            listOf(CloudListDownload(id = "uuid-1", localListId = 1, listName = "watchlist", isDefault = true))
        )
        coEvery { authService.fetchCloudContent("token", "user-1") } returns AuthResult.Success(emptyList())
        coEvery { authService.fetchCloudRatings("token", "user-1") } returns AuthResult.Error("Ratings failed")

        val result = syncService.performDownload("token", "user-1")

        assertIs<AuthResult.Error>(result)
        assertEquals("Ratings failed", (result as AuthResult.Error).message)
    }

    @Test
    fun `performDownload sets hasLocalChanges to false on success`() = runTest {
        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Success(emptyList())
        coEvery { authService.fetchCloudContent("token", "user-1") } returns AuthResult.Success(emptyList())
        coEvery { authService.fetchCloudRatings("token", "user-1") } returns AuthResult.Success(emptyList())
        coEvery { listEntityDao.deleteAll() } just runs
        coEvery { personalRatingDao.deleteAll() } just runs
        coEvery { listEntityDao.insertAll(any()) } just runs
        coEvery { contentEntityDao.insertAll(any()) } just runs
        coEvery { personalRatingDao.insertAll(any()) } just runs

        syncService.performDownload("token", "user-1")

        coVerify { settingsRepository.setHasLocalChanges(false) }
    }

    @Test
    fun `performDownload maps multiple lists with correct IDs`() = runTest {
        val cloudLists = listOf(
            CloudListDownload(id = "uuid-1", localListId = 1, listName = "watchlist", isDefault = true),
            CloudListDownload(id = "uuid-2", localListId = 2, listName = "watched", isDefault = true),
            CloudListDownload(id = "uuid-3", localListId = 3, listName = "custom", isDefault = false)
        )
        val cloudContent = listOf(
            CloudContentDownload(
                contentId = 100, mediaType = "MOVIE", listId = "uuid-1",
                createdAt = 1000L, title = "Movie 1"
            ),
            CloudContentDownload(
                contentId = 200, mediaType = "SHOW", listId = "uuid-2",
                createdAt = 2000L, title = "Show 1"
            ),
            CloudContentDownload(
                contentId = 300, mediaType = "MOVIE", listId = "uuid-3",
                createdAt = 3000L, title = "Movie 2"
            )
        )

        coEvery { authService.fetchCloudLists("token", "user-1") } returns AuthResult.Success(cloudLists)
        coEvery { authService.fetchCloudContent("token", "user-1") } returns AuthResult.Success(cloudContent)
        coEvery { authService.fetchCloudRatings("token", "user-1") } returns AuthResult.Success(emptyList())
        coEvery { listEntityDao.deleteAll() } just runs
        coEvery { personalRatingDao.deleteAll() } just runs
        coEvery { listEntityDao.insertAll(any()) } just runs
        coEvery { contentEntityDao.insertAll(any()) } just runs
        coEvery { personalRatingDao.insertAll(any()) } just runs

        syncService.performDownload("token", "user-1")

        coVerify {
            contentEntityDao.insertAll(match { entities ->
                entities.size == 3 &&
                    entities[0].contentId == 100 && entities[0].listId == 1 &&
                    entities[1].contentId == 200 && entities[1].listId == 2 &&
                    entities[2].contentId == 300 && entities[2].listId == 3
            })
        }
    }

    @Test
    fun `performUpload sends empty snapshot when no local data`() = runTest {
        coEvery { listEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { contentEntityDao.getAllSnapshot() } returns emptyList()
        coEvery { personalRatingDao.getAllSnapshot() } returns emptyList()
        coEvery { authService.uploadSnapshot(any(), any()) } returns AuthResult.Success(Unit)

        val result = syncService.performUpload("test-token")

        assertIs<AuthResult.Success<Unit>>(result)
        val requestSlot = slot<UploadSnapshotRequest>()
        coVerify { authService.uploadSnapshot("test-token", capture(requestSlot)) }
        assertTrue(requestSlot.captured.lists.isEmpty())
        assertTrue(requestSlot.captured.content.isEmpty())
        assertTrue(requestSlot.captured.ratings.isEmpty())
    }

    // endregion

    // region requestUpload additional

    @Test
    fun `requestUpload handles exception without clearing flag`() = testScope.runTest {
        every { tokenStorage.getAccessToken() } returns "test-token"
        coEvery { listEntityDao.getAllSnapshot() } throws RuntimeException("DB error")

        syncService.requestUpload()
        advanceUntilIdle()

        coVerify { settingsRepository.setHasLocalChanges(true) }
        coVerify(exactly = 0) { settingsRepository.setHasLocalChanges(false) }
    }

    // endregion
}
