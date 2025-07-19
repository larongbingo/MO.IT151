package noobnoob.mmdc

import com.google.gson.*
import io.ktor.serialization.gson.gson
import java.lang.reflect.Type
import java.util.UUID
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(UUID::class.java, JavaUuidDAdapter())
            registerTypeAdapter(Uuid::class.java, KotlinUuidDAdapter())
            registerTypeAdapter(Instant::class.java, KotlinInstantAdapter())
        }
    }
}

private class JavaUuidDAdapter : JsonSerializer<UUID> {
    override fun serialize(src: UUID, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

@OptIn(ExperimentalUuidApi::class)
private class KotlinUuidDAdapter : JsonSerializer<Uuid> {
    override fun serialize(src: Uuid, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

@OptIn(ExperimentalTime::class)
private class KotlinInstantAdapter : JsonSerializer<Instant> {
    override fun serialize(src: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return JsonPrimitive(src.toString())
    }
}