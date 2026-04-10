package auth.service

interface SyncService {
    fun requestUpload()
    suspend fun performUpload(accessToken: String): AuthResult<Unit>
    suspend fun performDownload(accessToken: String, userId: String): AuthResult<Unit>
    suspend fun hasCloudData(accessToken: String, userId: String): Boolean
}
