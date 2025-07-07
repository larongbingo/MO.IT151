package noobnoob.mmdc

import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import noobnoob.mmdc.database.UsersRepositoryDaoImpl
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        post("/api/user") {
            val userRepository = UsersRepositoryDaoImpl()
            val newUserRequestBody = call.receive<NewUserRequestBody>()
            userRepository.addUser(User(newUserRequestBody.username, newUserRequestBody.externalId))
            call.respondText("User added")
        }
    }
}

data class NewUserRequestBody(val username: String, val externalId: String)
