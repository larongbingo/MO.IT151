package noobnoob.mmdc.oauth.pkce

import kotlinx.serialization.Serializable

object UserSession {
    var sessionToken: String? = null
}

@Serializable
data class UserSessionResponse(
    val access_token: String,
    val expires_in: Int,
    val scope: String,
    val id_token: String,
    val token_type: String
)
