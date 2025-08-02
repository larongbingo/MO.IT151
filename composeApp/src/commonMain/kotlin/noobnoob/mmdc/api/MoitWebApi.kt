package noobnoob.mmdc.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import noobnoob.mmdc.oauth.pkce.UserSession
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MoitWebApi(
    val sessionToken: String,
    val host: String = "https://moit151-webapi-v4sv3.ondigitalocean.app",
    val httpClient: HttpClient = MoitHttpClient.httpClient
) {
    suspend fun getUser(): User? {
        val response = httpClient.get("$host/api/user") {
            header(HttpHeaders.Authorization, "Bearer ${UserSession.sessionToken}")
        }

        if (response.status.isSuccess()) {
            return response.body<User>()
        }

        return null
    }
}


@OptIn(ExperimentalUuidApi::class)
@Serializable
data class User(val id: Uuid, val username: String, val externalId: String)