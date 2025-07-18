@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package noobnoob.mmdc.database

import kotlinx.coroutines.Dispatchers
import noobnoob.mmdc.UploadedFile
import noobnoob.mmdc.UploadedFilesRepository
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

object UploadedFiles : UUIDTable("uploadedFiles") {
    val userId = reference("userId", Users.id)
    val uploadUri = text("uploadUri")
    val createdAt = datetime("createdAt")
    val validatedAt = datetime("validatedAt").nullable()
}

fun ResultRow.toUploadedFile(): UploadedFile {
    return UploadedFile(
        id = this[UploadedFiles.id].value.toKotlinUuid(),
        userId = this[UploadedFiles.userId].value.toKotlinUuid(),
        uploadUri = this[UploadedFiles.uploadUri],
        validatedAt = this[UploadedFiles.validatedAt]?.toInstant(ZoneOffset.UTC)?.toKotlinInstant(),
        createdAt = this[UploadedFiles.createdAt].toInstant(ZoneOffset.UTC).toKotlinInstant()
    )
}

class UploadedFileRepositoryDslImpl(val uploadedFilesTable: UploadedFiles) : UploadedFilesRepository {
    override suspend fun getAllFilesByUserId(userId: Uuid): List<UploadedFile> {
        val javaUserId = userId.toJavaUuid()
        val uploadedFiles = newSuspendedTransaction(Dispatchers.IO) {
            uploadedFilesTable
                .selectAll()
                .where { UploadedFiles.userId eq javaUserId }
                .map { it.toUploadedFile() }
        }
        return uploadedFiles
    }

    override suspend fun getByUserIdAndFileId(userId: Uuid, fileId: Uuid): UploadedFile? {
        val javaUserId = userId.toJavaUuid()
        val javaFileId = fileId.toJavaUuid()
        val uploadedFile = newSuspendedTransaction(Dispatchers.IO) {
            uploadedFilesTable
                .selectAll()
                .where { (UploadedFiles.id eq javaFileId) and (UploadedFiles.userId eq javaUserId) }
                .map { it.toUploadedFile() }
                .singleOrNull()
        }
        return uploadedFile
    }

    override suspend fun updateFile(file: UploadedFile) {
        val javaFileId = file.id.toJavaUuid()
        newSuspendedTransaction(Dispatchers.IO) {
            uploadedFilesTable.update({ UploadedFiles.id eq javaFileId }, limit = 1) {
                it[validatedAt] =
                    if (file.validatedAt != null)
                        LocalDateTime.ofInstant(file.validatedAt?.toJavaInstant(), ZoneOffset.UTC)
                    else
                        null
            }
        }
    }

    override suspend fun addFile(file: UploadedFile) {
        newSuspendedTransaction(Dispatchers.IO) {
            uploadedFilesTable.insert {
                it[id] = file.id.toJavaUuid()
                it[userId] = file.userId.toJavaUuid()
                it[uploadUri] = file.uploadUri
                it[validatedAt] =
                    if (file.validatedAt != null)
                        LocalDateTime.ofInstant(file.validatedAt?.toJavaInstant(), ZoneOffset.UTC)
                    else
                        null
                it[createdAt] = LocalDateTime.ofInstant(file.createdAt.toJavaInstant(), ZoneOffset.UTC)
            }
        }
    }
}
