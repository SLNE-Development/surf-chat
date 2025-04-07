plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.chat.bukkit.SurfChatBukkit")
    authors.add("SLNE Development")

    generateLibraryLoader(false)
}

dependencies {
    api(project(":surf-chat-core"))
    api(libs.surf.database)
}