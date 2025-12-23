import dev.slne.surf.surfapi.gradle.util.registerSoft

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
    implementation("dev.slne.surf:surf-redis:1.0.0-SNAPSHOT")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.chat.bukkit.BukkitMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    serverDependencies {
        registerSoft("MiniPlaceholders")
    }

    authors.add("red")
}