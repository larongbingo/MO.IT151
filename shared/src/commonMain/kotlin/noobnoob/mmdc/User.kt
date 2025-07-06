package noobnoob.mmdc

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class User(
    val username: String,
    val externalId: String,
    val id: Uuid = Uuid.random(),
)

interface UsersRepository {
    suspend fun addUser(user: User)
    suspend fun getByExternalId(externalId: String): User?
    suspend fun getByUsername(username: String): User?
}