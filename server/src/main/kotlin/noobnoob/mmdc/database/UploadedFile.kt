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

object UploadedFiles : UUIDTable("uploadedFiles") {
    val userId = reference("userId", Users.id)
    val uploadUri = text("uploadUri")
    val createdAt = datetime("createdAt")
    val validatedAt = datetime("validatedAt").nullable()
}

class UploadedFilesDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UploadedFilesDao>(UploadedFiles)
    val userId by UploadedFiles.userId
    val uploadUri by UploadedFiles.uploadUri
    val createdAt by UploadedFiles.createdAt
    val validatedAt by UploadedFiles.validatedAt
}

fun daoToModel(dao: UploadedFilesDao) = UploadedFile(
    id = dao.id.value.toKotlinUuid(),
    userId = dao.userId.toKotlinUuid(),
    uploadUri = dao.uploadUri,
    createdAt = dao.createdAt.toInstant(ZoneOffset.UTC).toKotlinInstant(),
    validatedAt = dao.validatedAt?.toInstant(ZoneOffset.UTC)?.toKotlinInstant()
)

fun ResultRow.toUploadedFile(): UploadedFile {
    return UploadedFile(
        id = this[UploadedFiles.id].value.toKotlinUuid(),
        userId = this[UploadedFiles.userId].toKotlinUuid(),
        uploadUri = this[UploadedFiles.uploadUri],
        validatedAt = this[UploadedFiles.validatedAt]?.toInstant(ZoneOffset.UTC)?.toKotlinInstant(),
        createdAt = this[UploadedFiles.createdAt].toInstant(ZoneOffset.UTC).toKotlinInstant()
    )
}

class UploadedFileRepositoryDaoImpl() : UploadedFilesRepository {
    override suspend fun getAllFilesByUserId(userId: Uuid): List<UploadedFile> {
        val files = newSuspendedTransaction(Dispatchers.IO) {
            UploadedFilesDao
                .find { UploadedFiles.userId eq userId.toJavaUuid() }
                .map(::daoToModel)
        }
        return files
    }

    override suspend fun getByUserIdAndFileId(userId: Uuid, fileId: Uuid): UploadedFile? {
        TODO("Not yet implemented")
    }

    override suspend fun addFile(file: UploadedFile) {
        TODO("Not yet implemented")
    }

    override suspend fun updateFile(file: UploadedFile) {
        TODO("Not yet implemented")
    }

}

class UploadedFileRepositoryDslImpl(val uploadedFilesTable: UploadedFiles) : UploadedFilesRepository {
    override suspend fun getAllFilesByUserId(userId: Uuid): List<UploadedFile> {
        val javaUserId = UUID.fromString(userId.toString())
        val uploadedFiles = newSuspendedTransaction(Dispatchers.IO) {
            uploadedFilesTable
                .selectAll()
                .where { UploadedFiles.id eq javaUserId }
                .map { it.toUploadedFile() }
        }
        return uploadedFiles
    }

    override suspend fun getByUserIdAndFileId(userId: Uuid, fileId: Uuid): UploadedFile? {
        val javaUserId = UUID.fromString(userId.toString())
        val javaFileId = UUID.fromString(fileId.toString())
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
        val javaFileId = UUID.fromString(file.id.toString())
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
                it[id] = UUID.fromString(file.id.toString())
                it[userId] = UUID.fromString(file.userId.toString())
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
