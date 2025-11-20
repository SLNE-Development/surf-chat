plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withCloudClientCommon()
}

dependencies {
    api(project(":surf-chat-core:surf-chat-core-common"))
}