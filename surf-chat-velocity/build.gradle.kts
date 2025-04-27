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

    implementation(libs.fastutil)
    implementation(kotlin("stdlib"))

    kapt(libs.velocity.api)
}