package noobnoob.mmdc

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import java.util.concurrent.TimeUnit

private val jwkProvider = JwkProviderBuilder("https://ewan.au.auth0.com/")
    .cached(10, 24, TimeUnit.HOURS)
    .rateLimited(10, 1, TimeUnit.MINUTES)
    .build()

private fun validateCredentials(credential: JWTCredential): UserIdPrincipal? {
    val containsAudience = credential.payload.audience.contains("https://ewan/api")

    if (containsAudience) {
        return UserIdPrincipal(credential.payload.getClaim("sub").asString())
    }

    return null
}

fun Application.configureAuthorization() {
    install(Authentication) {
        jwt("auth0") {
            verifier(jwkProvider, "https://ewan.au.auth0.com/")
            validate { credential -> validateCredentials(credential) }
        }
    }
}