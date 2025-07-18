@file:OptIn(ExperimentalTime::class)

package noobnoob.mmdc

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import noobnoob.mmdc.database.UploadedFileRepositoryDslImpl
import noobnoob.mmdc.database.UploadedFiles
import noobnoob.mmdc.database.Users
import noobnoob.mmdc.database.UsersRepositoryDslImpl
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        authenticate("auth0") {
            get("/api/user") {
                val userRepository: UsersRepository = UsersRepositoryDslImpl(Users)

                val principal = call.principal<UserIdPrincipal>()
                if (principal == null) {
                    call.respond(status = HttpStatusCode.BadRequest, message = Response("Invalid JWT"))
                    return@get
                }
                val userId = principal.name

                val userWithMatchingExternalId = userRepository.getByUsername(userId)
                if (userWithMatchingExternalId == null) {
                    call.respond(status = HttpStatusCode.NotFound, message = Response("User not found"))
                } else {
                    call.respond(status = HttpStatusCode.OK, message = userWithMatchingExternalId)
                }
            }
            post("/api/user") {
                val userRepository: UsersRepository = UsersRepositoryDslImpl(Users)

                val principal = call.principal<UserIdPrincipal>()
                if (principal == null) {
                    call.respond(status = HttpStatusCode.BadRequest, message = Response("Invalid JWT"))
                    return@post
                }
                val externalId = principal.name

                val userWithMatchingExternalId = userRepository.getByExternalId(externalId)
                if (userWithMatchingExternalId != null) {
                    call.respond(status = HttpStatusCode.BadRequest, message = Response("External Id already taken"))
                    return@post
                }


                val newUserRequestBody = call.receive<NewUserRequestBody>()
                val userWithMatchingUsername = userRepository.getByUsername(newUserRequestBody.username)
                if (userWithMatchingUsername != null) {
                    call.respond(status = HttpStatusCode.BadRequest, message = Response("Username already taken"))
                    return@post
                }

                userRepository.addUser(User(newUserRequestBody.username, externalId))
                call.respondText("User added")
            }
        }
        authenticate("auth0WithUser") {
            get("/api/files") {
                val filesRepository = UploadedFileRepositoryDslImpl(UploadedFiles)
                // forced null access since it shouldn't proceed if the user isn't registered
                val userId = call.principal<UserIdPrincipal>()!!.name
                val files = filesRepository.getAllFilesByUserId(Uuid.parse(userId))
                call.respond(files)
            }
            post("/api/files") {
                val filesRepository = UploadedFileRepositoryDslImpl(UploadedFiles)
                val storageService = Storage()
                // forced null access since it shouldn't proceed if the user isn't registered
                val userId = call.principal<UserIdPrincipal>()!!.name
                val fileId = Uuid.random()
                val url = storageService.getPresignedUrl("$userId/$fileId")
                filesRepository.addFile(UploadedFile(fileId, Uuid.parse(userId), url.toString()))
                call.respond(PresignedUrlResponse(url!!, fileId.toString()))
            }
        }
    }
}

data class NewUserRequestBody(val username: String)
data class Response(val message: String)
data class PresignedUrlResponse(val url: String, val fileId: String)
