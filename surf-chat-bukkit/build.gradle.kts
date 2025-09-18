plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    api(project(":surf-chat-core"))

    compileOnly(libs.playerholder.api)
    compileOnly(libs.miniplaceholder.api)

    runtimeOnly(project(":surf-chat-fallback"))
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.chat.bukkit.BukkitMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    serverDependencies {
        register("MiniPlaceholders")
    }

    authors.add("red")
}