plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
    kotlin("kapt")
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    api(project(":surf-chat-core"))

    kapt(libs.velocity.api)
}