package noobnoob.mmdc

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import noobnoob.mmdc.database.Users
import noobnoob.mmdc.database.UsersRepositoryDslImpl
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        authenticate("auth0") {
            post("/api/user") {
                val principal = call.principal<UserIdPrincipal>()
                if (principal == null) {
                    call.respond(status = HttpStatusCode.BadRequest, message = ResponseTest("Invalid JWT"))
                    return@post
                }
                val externalId = principal.name
                val userRepository: UsersRepository = UsersRepositoryDslImpl(Users)
                val newUserRequestBody = call.receive<NewUserRequestBody>()
                userRepository.addUser(User(newUserRequestBody.username, externalId))
                call.respondText("User added")
            }
        }
    }
}

data class NewUserRequestBody(val username: String)
data class ResponseTest(val message: String)
