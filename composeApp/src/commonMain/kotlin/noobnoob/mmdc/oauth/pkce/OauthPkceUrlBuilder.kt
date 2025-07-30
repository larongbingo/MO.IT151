package noobnoob.mmdc.oauth.pkce

import kotlin.io.encoding.Base64
import kotlin.io.encoding.Base64.PaddingOption
import kotlin.random.Random

fun buildOauthPkceAuthorizationUrl(
    domain: String,
    clientId: String,
    redirectUri: String,
    audience: String,
    scope: String,
    state: String = "TESTING"
): OauthPkceUrl {
    val responseType = "code"
    val codes = generateCodeVerifierAndChallenge()
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

    return OauthPkceUrl(url, codes)
}

fun buildOauthPkceGrantTokenUrl(domain: String, clientId: String, code: String, redirectUri: String, codes: OauthPkceCodes): String {
    val grantType = "authorization_code"

    val url = "https://$domain/oauth/token?" +
            "grantType=$grantType" +
            "&client_id=$clientId" +
            "&code_verifier=${codes.codeVerifier}" +
            "&code=$code" +
            "&redirect_uri=$redirectUri"

    return url
}

private fun generateCodeVerifierAndChallenge(): OauthPkceCodes {
    val bytes = Random.nextBytes(32)
    val codeVerifier = Base64.withPadding(PaddingOption.ABSENT).encode(bytes).cleanUpCodeString()
    val hash = sha256(bytes)
    val codeChallenge = Base64.withPadding(PaddingOption.ABSENT).encode(hash).cleanUpCodeString()
    return OauthPkceCodes(codeVerifier, codeChallenge)
}

private fun String.cleanUpCodeString(): String =
    this.replace('/', '_').replace('+', '-')

data class OauthPkceCodes(val codeVerifier: String, val codeChallenge: String)
data class OauthPkceUrl(val url: String, val codes: OauthPkceCodes)