plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

dependencies {
    api(project(":surf-chat-api"))
    compileOnly(libs.paper.api)
}