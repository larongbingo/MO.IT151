package noobnoob.mmdc.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import noobnoob.mmdc.oauth.pkce.UserSession
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MoitWebApi(
    val sessionToken: String = UserSession.sessionToken!!,
    val host: String = "https://moit151-webapi-v4sv3.ondigitalocean.app",
    val httpClient: HttpClient = MoitHttpClient.httpClient
) {
    suspend fun getUser(): User? {
        val response = httpClient.get("$host/api/user") {
            header(HttpHeaders.Authorization, "Bearer $sessionToken")
        }

        if (response.status.isSuccess()) {
            return response.body<User>()
        }

        return null
    }

    // TODO: this api has validation messages like "username already taken", add later
    suspend fun createUser(username: String): NewUser? {
        val response = httpClient.post("$host/api/user") {
            header(HttpHeaders.Authorization, "Bearer $sessionToken")
            setBody(CreateUserBody(username))
            contentType(ContentType.Application.Json)
        }

        if (response.status.isSuccess()) {
            return response.body<NewUser>()
        }

        return null
    }
}


@OptIn(ExperimentalUuidApi::class)
@Serializable
data class User(val id: Uuid, val username: String, val externalId: String)

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class NewUser(val id: Uuid)

@Serializable
data class CreateUserBody(val username: String)