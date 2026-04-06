import dev.slne.surf.api.gradle.util.registerRequired
import dev.slne.surf.api.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    api(projects.surfChatCore.surfChatCorePaper)

    compileOnly(libs.playerholder.api)
    compileOnly(libs.miniplaceholder.api)
    compileOnly(libs.luckperms.api)

    implementation("de.maxbossing:kotlin-discord-webhook:1") {
        exclude("org.jetbrains.kotlin")
    }
    implementation("com.openai:openai-java:4.11.0") {
        exclude(group = "org.apache.httpcomponents.client5")
        exclude(group = "org.apache.httpcomponents.core5")
        exclude(group = "org.jetbrains.kotlin")
    }
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