plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "noobnoob.mmdc"
version = "1.0.0"
application {
    mainClass.set("noobnoob.mmdc.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation("io.ktor:ktor-server-core:3.2.0")
    implementation(libs.ktor.server.swagger)
    implementation("io.ktor:ktor-server-openapi:3.2.0")
    implementation("io.ktor:ktor-server-compression:3.2.0")
    implementation("io.ktor:ktor-client-logging:3.2.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.2.0")
    implementation("io.ktor:ktor-serialization-gson:3.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.0")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.h2database:h2:2.3.232")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.0")
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.61.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.0")
    implementation("org.jetbrains.exposed:exposed-migration:0.61.0")
    implementation("io.ktor:ktor-server-host-common:3.2.0")
    implementation("io.ktor:ktor-server-status-pages:3.2.0")
    implementation("io.ktor:ktor-server-auth:3.2.0")
    implementation("io.ktor:ktor-client-core:3.2.0")
    implementation("io.ktor:ktor-client-apache:3.2.0")
    implementation("io.ktor:ktor-server-auth-jwt:3.2.0")
    implementation(awssdk.services.s3)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}

ktor {
    docker {
        localImageName.set("moit151-kotlin")
    }
}