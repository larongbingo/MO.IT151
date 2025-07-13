@file:OptIn(ExperimentalUuidApi::class)

package noobnoob.mmdc.database

import io.ktor.util.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import noobnoob.mmdc.User
import noobnoob.mmdc.UsersRepository
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

object Users : UUIDTable("users") {
    val username = text("username").uniqueIndex()
    val externalId = text("externalId").uniqueIndex()
}

@OptIn(ExperimentalUuidApi::class)
fun ResultRow.toUser(): User {
    return User(
        id = Uuid.parse(this[Users.id].toString()),
        username = this[Users.username],
        externalId =  this[Users.externalId],
    )
}

class UsersRepositoryDslImpl(val usersTable: Users) : UsersRepository {
    override suspend fun getByExternalId(externalId: String): User? {
        val userWithSameExternalId = newSuspendedTransaction(Dispatchers.IO) {
            usersTable
                .selectAll()
                .where { Users.externalId eq externalId }
                .map { it.toUser() }
                .singleOrNull()
        }
        return userWithSameExternalId
    }

    override suspend fun getByUsername(username: String): User? {
        val userWithSameUsername = newSuspendedTransaction(Dispatchers.IO) {
            usersTable
                .selectAll()
                .where {Users.username eq username}
                .map { it.toUser() }
                .singleOrNull()
        }
        return userWithSameUsername
    }

    override suspend fun addUser(user: User) {
        newSuspendedTransaction(Dispatchers.IO) {
            val result = usersTable.insert {
                it[id] = user.id.toJavaUuid()
                it[username] = user.username
                it[externalId] = user.externalId
            }
        }
    }
}
