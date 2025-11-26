plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

surfVelocityApi {
    withCloudClientVelocity()
}

velocityPluginFile {
    main = "dev.slne.surf.chat.velocity.VelocityMain"
    authors = listOf("red")
}

dependencies {
    api(project(":surf-chat-core:surf-chat-core-client"))
}