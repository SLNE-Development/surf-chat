import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")

}

repositories {
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    api(project(":surf-chat-core:surf-chat-core-client"))
    compileOnly(libs.miniplaceholder.api)
    compileOnly("net.luckperms:api:5.4")
    implementation("com.github.BinaryWriter:discord-webhooks:1.0") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-json")
    }
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.chat.paper.PaperMain")
    bootstrapper("dev.slne.surf.chat.paper.PaperBootstrap")
    foliaSupported(true)
    generateLibraryLoader(false)
    withCloudClientPaper()

    serverDependencies {
        registerSoft("MiniPlaceholders")
    }

    authors.add("red")
}

tasks.shadowJar {
    configurations = listOf(project.configurations.runtimeClasspath.get())
    relocate("com.fasterxml.jackson", "dev.slne.surf.chat.shadow.jackson")
    mergeServiceFiles("META-INF")
}
