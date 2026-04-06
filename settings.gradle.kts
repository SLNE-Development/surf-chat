include("surf-chat-api")
include("surf-chat-core:surf-chat-core-common")
include("surf-chat-core:surf-chat-core-paper")
include("surf-chat-paper")
include("surf-chat-microservice")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}
