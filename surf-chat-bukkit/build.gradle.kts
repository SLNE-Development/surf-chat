import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.chat.bukkit.SurfChatBukkit")
    authors.add("red")

    serverDependencies {
        registerRequired("LuckPerms")
    }

    generateLibraryLoader(false)
}

dependencies {
    api(project(":surf-chat-core"))
    api(libs.surf.database)

    compileOnly(libs.luckperms.api)
}