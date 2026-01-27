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
    compileOnly(libs.luckperms.api)

    runtimeOnly(project(":surf-chat-fallback"))
    implementation("de.maxbossing:kotlin-discord-webhook:1") {
        exclude("org.jetbrains.kotlin")
    }
    implementation("com.openai:openai-java:4.11.0") {
        exclude(group = "org.apache.httpcomponents.client5")
        exclude(group = "org.apache.httpcomponents.core5")
        exclude(group = "org.jetbrains.kotlin")
    }
    compileOnly("dev.slne.surf.settings:surf-settings-api:1.21.11-2.0.0-SNAPSHOT")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.chat.bukkit.BukkitMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    withCorePaper()
    withSurfRedis()

    serverDependencies {
        registerSoft("MiniPlaceholders")
        registerSoft("surf-settings-paper")
    }

    authors.add("red")
}