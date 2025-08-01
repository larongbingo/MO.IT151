package noobnoob.mmdc.oauth.pkce

import io.ktor.http.Parameters
import io.ktor.http.parameters
import kotlin.io.encoding.Base64
import kotlin.io.encoding.Base64.PaddingOption
import kotlin.random.Random

class OauthPkceUrlBuilder(
    val domain: String,
    val clientId: String,
    val redirectUri: String,
    val audience: String,
    val scope: String,
    val state: String = "TESTING"
) {
    val codes = generateCodeVerifierAndChallenge()

    fun buildAuthorizationUrl(): String {
        val responseType = "code"
        val codeChallenge = codes.codeChallenge
        val codeChallengeMethod = "S256"

        val url = "https://$domain/authorize?" +
                "response_type=$responseType" +
                "&code_challenge=$codeChallenge" +
                "&code_challenge_method=$codeChallengeMethod" +
                "&client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&scope=$scope" +
                "&state=$state" +
                "&audience=$audience"

        return url
    }

    fun buildGrantByAuthorizationUrl(code: String): OauthPkceAuthorizationGrantUrl {
        val grantType = "authorization_code"

        val url = "https://$domain/oauth/token"

        return OauthPkceAuthorizationGrantUrl(url,  parameters {
            append("redirect_uri", redirectUri)
            append("code", code)
            append("grant_type", grantType)
            append("code_verifier", codes.codeVerifier)
            append("client_id", clientId)
        })
    }
}

private fun generateCodeVerifierAndChallenge(): OauthPkceCodes {
    val bytes = Random.nextBytes(32)
    val codeVerifier = Base64.withPadding(PaddingOption.ABSENT).encode(bytes).cleanUpCodeString()
    val codeChallenge = platformSha256(codeVerifier).cleanUpCodeString()
    return OauthPkceCodes(codeVerifier, codeChallenge)
}

private fun String.cleanUpCodeString(): String =
    this.replace('/', '_').replace('+', '-').replace("=", "")


data class OauthPkceCodes(val codeVerifier: String, val codeChallenge: String)
data class OauthPkceUrl(val url: String, val codes: OauthPkceCodes)
data class OauthPkceAuthorizationGrantUrl(val url: String, val body: Parameters)
data class OauthPkceAuthorizationGrantBody(
    val grant_type: String,
    val client_id: String,
    val code_verifier: String,
    val code: String,
    val redirect_uri: String
)

