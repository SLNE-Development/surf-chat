plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

dependencies {
    api(project(":surf-chat-core"))
    api("dev.slne.surf:surf-database-r2dbc:1.0.0-SNAPSHOT")
}