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
    implementation("com.github.BinaryWriter:discord-webhooks:1.0") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-json")
    }
    implementation("com.openai:openai-java:4.11.0") {
        exclude(group = "org.apache.httpcomponents.client5")
        exclude(group = "org.apache.httpcomponents.core5")
        exclude(group = "org.jetbrains.kotlin")
    }
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