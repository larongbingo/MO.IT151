package noobnoob.mmdc.oauth.pkce

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import kotlin.io.encoding.Base64

actual fun platformSha256(text: String): String {
    val bytes: ByteArray = text.toByteArray(StandardCharsets.US_ASCII)
    val md = MessageDigest.getInstance("SHA-256")
    md.update(bytes, 0, bytes.size)
    val digest = md.digest()
    val challenge = Base64.Default.encode(digest)
    return challenge
}