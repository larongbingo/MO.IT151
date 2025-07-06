@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package noobnoob.mmdc.database

import noobnoob.mmdc.UploadedFile
import noobnoob.mmdc.UploadedFilesRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.TimeZone
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object UploadedFiles : Table("uploadedFiles") {
    val id = uuid("id")
    val userId = reference("userId", Users.id)
    val uploadUri = text("uploadUri")
    val createdAt = datetime("createdAt")
    val validatedAt = datetime("validatedAt").nullable()
    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toUploadedFile(): UploadedFile {
    return UploadedFile(
        id = Uuid.parse(this[UploadedFiles.id].toString()),
        userId = Uuid.parse(this[UploadedFiles.userId].toString()),
        uploadUri = this[UploadedFiles.uploadUri],
        validatedAt = this[UploadedFiles.validatedAt]?.toInstant(ZoneOffset.UTC)?.toKotlinInstant(),
        createdAt = this[UploadedFiles.createdAt].toInstant(ZoneOffset.UTC).toKotlinInstant()
    )
}

class UploadedFileRepositoryImpl(val uploadedFilesTable: UploadedFiles) : UploadedFilesRepository {
    override suspend fun getAllFilesByUserId(userId: Uuid): List<UploadedFile> {
        val javaUserId = UUID.fromString(userId.toString())
        val uploadedFiles = transaction {
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
        val uploadedFile = transaction {
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
        transaction {
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
        transaction {
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
