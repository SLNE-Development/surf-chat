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