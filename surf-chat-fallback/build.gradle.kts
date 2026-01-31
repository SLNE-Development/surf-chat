plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

surfRawPaperApi {
    withSurfDatabaseR2dbc("1.3.0", "dev.slne.surf.chat.libs.db")
    withCoreCommon()
}

dependencies {
    api(project(":surf-chat-core"))
}