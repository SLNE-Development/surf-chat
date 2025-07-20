plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    api(project(":surf-chat-core"))
    compileOnly(libs.playerholder.api)
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.chat.bukkit.BukkitMain")
    authors.add("red")
    generateLibraryLoader(false)
}