package noobnoob.mmdc

import com.google.gson.*
import io.ktor.serialization.gson.gson
import java.lang.reflect.Type
import java.util.UUID
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(UUID::class.java, JavaUuidDAdapter())
            registerTypeAdapter(Uuid::class.java, KotlinUuidDAdapter())
        }
    }
}

private class JavaUuidDAdapter : JsonSerializer<UUID>, JsonDeserializer<UUID> {
    override fun serialize(src: UUID, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): UUID {
        return UUID.fromString(json.asString)
    }
}

@OptIn(ExperimentalUuidApi::class)
private class KotlinUuidDAdapter : JsonSerializer<Uuid>, JsonDeserializer<Uuid> {
    override fun serialize(src: Uuid, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Uuid? {
        return Uuid.parse(json!!.asString)
    }
}