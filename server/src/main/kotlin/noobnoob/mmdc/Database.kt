package noobnoob.mmdc

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import java.sql.DriverManager

fun Application.configureDatabases() {
    Database.connect(
        url = System.getenv("MOIT151_POSTGRES_CONNECTION_URL")
    )
}
