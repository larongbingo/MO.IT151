package noobnoob.mmdc

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import kotlinx.coroutines.runBlocking
import noobnoob.mmdc.database.Users
import noobnoob.mmdc.database.UsersRepositoryDslImpl
import java.util.concurrent.TimeUnit
import kotlin.uuid.ExperimentalUuidApi

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

@OptIn(ExperimentalUuidApi::class)
private suspend fun validateCredentialsAndUser(credential: JWTCredential): UserIdPrincipal? {
    val credentials = validateCredentials(credential)
    if (credentials == null) {
        return null
    }

    val userRepository = UsersRepositoryDslImpl(Users)
    val user = userRepository.getByExternalId(credentials.name)
    if (user == null) {
        return null
    }

    return UserIdPrincipal(user.id.toString())
}

fun Application.configureAuthorization() {
    install(Authentication) {
        jwt("auth0") {
            verifier(jwkProvider, "https://ewan.au.auth0.com/")
            validate { credential -> validateCredentials(credential) }
        }
        jwt("auth0WithUser") {
            verifier(jwkProvider, "https://ewan.au.auth0.com/")
            validate { credential -> validateCredentialsAndUser(credential) }
        }
    }
}