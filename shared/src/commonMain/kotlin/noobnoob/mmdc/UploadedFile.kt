package noobnoob.mmdc

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class UploadedFile(
    val id: Uuid,
    val userId: Uuid,
    val uploadUri: String,
    val createdAt: Instant = Clock.System.now(),
    var validatedAt: Instant? = null,) {
    fun validate() {
        validatedAt = Clock.System.now()
    }
}

@OptIn(ExperimentalUuidApi::class)
interface UploadedFilesRepository {
    suspend fun getAllFilesByUserId(userId: Uuid): List<UploadedFile>
    suspend fun getByUserIdAndFileId(userId: Uuid, fileId: Uuid): UploadedFile?
    suspend fun addFile(file: UploadedFile)
    suspend fun updateFile(file: UploadedFile)
}