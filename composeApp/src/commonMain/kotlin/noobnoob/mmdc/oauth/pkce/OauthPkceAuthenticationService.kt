package noobnoob.mmdc.oauth.pkce

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json

class OauthPkceAuthenticationService(val urlBuilder: OauthPkceUrlBuilder) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun fetchSessionTokensWithAuthorizationCode(authorizationCode: String): UserSessionResponse? {
        val request = urlBuilder.buildGrantByAuthorizationUrl(authorizationCode)
        val response = client.submitForm(request.url, request.body)

        if (response.status.isSuccess()) {
            val body = response.body<UserSessionResponse>()
            return body
        }

        return null
    }
}