package auth.service

import auth.model.UploadSnapshotRequest
import auth.model.toCloudContentUpload
import auth.model.toCloudListUpload
import auth.model.toCloudRatingUpload
import auth.model.toContentEntity
import auth.model.toListEntity
import auth.model.toPersonalRatingEntity
import auth.platform.TokenStorage
import co.touchlab.kermit.Logger
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.dao.PersonalRatingDao
import database.repository.SettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SyncServiceImpl(
    private val authService: SupabaseAuthService,
    private val tokenStorage: TokenStorage,
    private val contentEntityDao: ContentEntityDao,
    private val listEntityDao: ListEntityDao,
    private val personalRatingDao: PersonalRatingDao,
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope
) : SyncService {

    private val log = Logger.withTag("SyncService")
    private var uploadJob: Job? = null

    override fun requestUpload() {
        settingsRepository.setHasLocalChanges(true)
        val accessToken = tokenStorage.getAccessToken() ?: return
        uploadJob?.cancel()
        uploadJob = scope.launch {
            delay(UPLOAD_DEBOUNCE_MS)
            try {
                val result = performUpload(accessToken)
                if (result is AuthResult.Success) {
                    settingsRepository.setHasLocalChanges(false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.e(e) { "Upload failed with exception" }
            }
        }
    }

    override suspend fun performUpload(accessToken: String): AuthResult<Unit> {
        val lists = listEntityDao.getAllSnapshot()
        val content = contentEntityDao.getAllSnapshot()
        val ratings = personalRatingDao.getAllSnapshot()

        val request = UploadSnapshotRequest(
            lists = lists.map { it.toCloudListUpload() },
            content = content.map { it.toCloudContentUpload() },
            ratings = ratings.map { it.toCloudRatingUpload() }
        )

        log.d { "Uploading snapshot: ${lists.size} lists, ${content.size} items, ${ratings.size} ratings" }
        return authService.uploadSnapshot(accessToken, request)
    }

    override suspend fun performDownload(accessToken: String, userId: String): AuthResult<Unit> {
        val listsResult = authService.fetchCloudLists(accessToken, userId)
        if (listsResult is AuthResult.Error) return listsResult

        val contentResult = authService.fetchCloudContent(accessToken, userId)
        if (contentResult is AuthResult.Error) return contentResult

        val ratingsResult = authService.fetchCloudRatings(accessToken, userId)
        if (ratingsResult is AuthResult.Error) return ratingsResult

        val cloudLists = (listsResult as AuthResult.Success).data
        val cloudContent = (contentResult as AuthResult.Success).data
        val cloudRatings = (ratingsResult as AuthResult.Success).data

        val listIdMap = cloudLists.associate { it.id to it.localListId }

        // Clear local data — deleteAll on lists cascades to content via FK
        listEntityDao.deleteAll()
        personalRatingDao.deleteAll()

        // Insert cloud data — lists first (FK parent), then content, then ratings
        listEntityDao.insertAll(cloudLists.map { it.toListEntity() })
        contentEntityDao.insertAll(
            cloudContent.mapNotNull { content ->
                val localListId = listIdMap[content.listId] ?: return@mapNotNull null
                content.toContentEntity(localListId)
            }
        )
        personalRatingDao.insertAll(cloudRatings.map { it.toPersonalRatingEntity() })

        settingsRepository.setHasLocalChanges(false)
        log.d {
            "Downloaded snapshot: ${cloudLists.size} lists, ${cloudContent.size} items, ${cloudRatings.size} ratings"
        }
        return AuthResult.Success(Unit)
    }

    override suspend fun hasCloudData(accessToken: String, userId: String): Boolean {
        val result = authService.fetchCloudLists(accessToken, userId)
        return result is AuthResult.Success && result.data.isNotEmpty()
    }

    companion object {
        private const val UPLOAD_DEBOUNCE_MS = 2000L
    }
}
