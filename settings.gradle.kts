include("surf-chat-api")
include("surf-chat-core")
include("surf-chat-paper")
include("surf-chat-microservice")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.surfapi.gradle.settings") version "1.21.11+"
}
include("surf-chat-core:surf-chat-core-common")
include("surf-chat-core:surf-chat-core-paper")