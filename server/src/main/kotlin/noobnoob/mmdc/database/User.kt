@file:OptIn(ExperimentalUuidApi::class)

package noobnoob.mmdc.database

import noobnoob.mmdc.User
import noobnoob.mmdc.UsersRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object Users : Table("users") {
    val id = uuid("id")
    val username = text("username").uniqueIndex()
    val externalId = text("externalId").uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

@OptIn(ExperimentalUuidApi::class)
fun ResultRow.toUser(): User {
    return User(
        id = Uuid.parse(this[Users.id].toString()),
        username = this[Users.username],
        externalId =  this[Users.externalId],
    )
}

class UsersRepositoryImpl(val usersTable: Users) : UsersRepository {
    override suspend fun getByExternalId(externalId: String): User? {
        val userWithSameExternalId = transaction {
            usersTable
                .selectAll()
                .where { Users.externalId eq externalId }
                .map { it.toUser() }
                .singleOrNull()
        }
        return userWithSameExternalId
    }

    override suspend fun getByUsername(username: String): User? {
        val userWithSameUsername = transaction {
            usersTable
                .selectAll()
                .where {Users.username eq username}
                .map { it.toUser() }
                .singleOrNull()
        }
        return userWithSameUsername
    }

    override suspend fun addUser(user: User) {
        transaction {
            usersTable.insert {
                it[id] = UUID.fromString(user.id.toString())
                it[username] = user.username
                it[externalId] = user.externalId
            }
        }
    }
}
