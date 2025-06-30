plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

version = "2.0.0-dev"

velocityPluginFile {
    id = "surf-chat-velocity"
    main = "dev.slne.surf.chat.velocity.VelocityMain"
    name = "SurfChatVelocity"
    authors = listOf("red")
    description = "SurfChat Velocity Plugin"
    version = "${project.version}"
}

dependencies {
    api(project(":surf-chat-core"))
    compileOnly(libs.packetevents.velocity)
    compileOnly(libs.luckperms.api)

    runtimeOnly(project(":surf-chat-fallback"))
}

tasks.shadowJar {
    archiveFileName = "surf-chat-velocity-$version.jar"
}