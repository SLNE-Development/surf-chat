plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

velocityPluginFile {
    id = "surf-chat-velocity"
    main = "dev.slne.surf.chat.velocity.VelocityMain"
    name = "SurfChatVelocity"
    authors = listOf("red")
    version = "${project.version}"
}

dependencies {
    api(project(":surf-chat-core"))
}