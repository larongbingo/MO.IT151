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
import io.ktor.server.routing.put
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
                call.respond(HttpStatusCode.OK, files)
            }
            post("/api/files") {
                val filesRepository = UploadedFileRepositoryDslImpl(UploadedFiles)
                val storageService = Storage()
                // forced null access since it shouldn't proceed if the user isn't registered
                val userId = call.principal<UserIdPrincipal>()!!.name
                val fileId = Uuid.random()
                val url = storageService.getUploadPresignedUrl("$userId/$fileId")
                filesRepository.addFile(UploadedFile(fileId, Uuid.parse(userId), url.toString()))
                call.respond(HttpStatusCode.Created, UploadPresignedUrlResponse(url!!, fileId.toString()))
            }
            put("/api/files/{fileId}") {
                val fileRepository = UploadedFileRepositoryDslImpl(UploadedFiles)
                val storageService = Storage()
                val userId = Uuid.parse(call.principal<UserIdPrincipal>()!!.name)
                if (call.pathParameters["fileId"].isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, Response("FileId is empty"))
                    return@put
                }
                val fileId = Uuid.parse(call.pathParameters["fileId"]!!)
                val file = fileRepository.getByUserIdAndFileId(userId, fileId)
                if (file == null || file.validatedAt != null) {
                    call.respond(Response("Invalid FileId"))
                    return@put
                }
                val key = "$userId/$fileId"
                val isObjectInStorage = storageService.objectExistsByKey(key)
                if (isObjectInStorage) {
                    file.validate()
                    fileRepository.updateFile(file)
                    call.respond(HttpStatusCode.Found)
                    return@put
                }
                call.respond(HttpStatusCode.NotFound)
            }
            get("/api/files/{fileId}") {
                val fileRepository = UploadedFileRepositoryDslImpl(UploadedFiles)
                val storageService = Storage()
                val userId = Uuid.parse(call.principal<UserIdPrincipal>()!!.name)
                if (call.pathParameters["fileId"].isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, Response("FileId is empty"))
                    return@get
                }
                val fileId = Uuid.parse(call.pathParameters["fileId"]!!)
                val file = fileRepository.getByUserIdAndFileId(userId, fileId)
                if (file == null || file.validatedAt == null) {
                    call.respond(Response("Invalid FileId"))
                    return@get
                }
                val key = "$userId/$fileId"
                val downloadUrl = storageService.getDownloadPresignedUrl(key)
                if (downloadUrl == null) {
                    call.respond(HttpStatusCode.NotFound, Response("Invalid FileId"))
                } else {
                    call.respond(HttpStatusCode.Found, DownloadPresignedUrlResponse(downloadUrl))
                }
            }
        }
    }
}

data class NewUserRequestBody(val username: String)
data class Response(val message: String)
data class UploadPresignedUrlResponse(val url: String, val fileId: String)
data class DownloadPresignedUrlResponse(val url: String)
