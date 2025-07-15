package noobnoob.mmdc

import io.ktor.server.application.Application
import noobnoob.mmdc.database.UploadedFiles
import noobnoob.mmdc.database.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager

fun Application.configureDatabases() {
    Database.connect(
        url = System.getenv("MOIT151_POSTGRES_CONNECTION_URL")
    )

    // No migration tooling, sketchy on runtime migration for now
    // Blocking transaction since we want to complete this first before allowing app code to run
    transaction {
        SchemaUtils.create(UploadedFiles, Users)
    }
}
