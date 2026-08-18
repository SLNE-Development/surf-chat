import dev.slne.surf.api.gradle.util.registerRequired
import dev.slne.surf.api.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    api(projects.surfChatCore.surfChatCoreClient)

    compileOnly(libs.playerholder.api)
    compileOnly(libs.miniplaceholder.api)
    compileOnly(libs.luckperms.api)

    compileOnly("dev.slne.surf.settings:surf-settings-api:+")
    compileOnly("dev.slne.surf.punish:surf-punish-api-common:+")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.chat.paper.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    withCorePaper()
    withSurfRedis()

    serverDependencies {
        registerSoft("MiniPlaceholders")
        registerSoft("surf-settings-paper")
        registerRequired("LuckPerms")
        registerRequired("surf-punish-paper")
    }

    authors.add("red")
}