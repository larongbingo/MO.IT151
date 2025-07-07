@file:OptIn(ExperimentalUuidApi::class)

package noobnoob.mmdc.database

import kotlinx.coroutines.Dispatchers
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

fun daoToModel(dao: UsersDao) = User(
    id = dao.id.value.toKotlinUuid(),
    username = dao.username,
    externalId = dao.externalId
)

class UsersDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UsersDao>(Users)
    var username by Users.username
    var externalId by Users.externalId
}

class UsersRepositoryDaoImpl() : UsersRepository {
    override suspend fun addUser(user: User): Unit = newSuspendedTransaction(Dispatchers.IO) {
        UsersDao.new {
            username = user.username
            externalId = user.externalId
        }
    }

    override suspend fun getByExternalId(externalId: String): User? = newSuspendedTransaction(Dispatchers.IO) {
        UsersDao.find { Users.externalId eq externalId }.map(::daoToModel).singleOrNull()
    }

    override suspend fun getByUsername(username: String): User? = newSuspendedTransaction(Dispatchers.IO) {
        UsersDao.find { Users.username eq username }.map(::daoToModel).singleOrNull()
    }

}

class UsersRepositoryDslImpl(val usersTable: Users) : UsersRepository {
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
