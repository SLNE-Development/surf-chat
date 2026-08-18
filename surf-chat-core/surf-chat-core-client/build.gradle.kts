import dev.slne.surf.api.gradle.util.slneReleases
import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.core")
    id("dev.slne.surf.microservice")
}

surfCoreApi {
    withCoreCommon()
    withSurfRedis()
}

surfMicroservice {
    withRabbitModule(RabbitModule.CLIENT_API)
}

dependencies {
    api(projects.surfChatCore.surfChatCoreCommon)

    compileOnly(libs.luckperms.api)
    compileOnly("dev.slne.surf.settings:surf-settings-api:+")
    compileOnly("dev.slne.surf.punish:surf-punish-api-common:+")

    api("com.openai:openai-java:4.42.0") {
        exclude(group = "org.apache.httpcomponents.client5")
        exclude(group = "org.apache.httpcomponents.core5")
        exclude(group = "org.jetbrains.kotlin")
    }
}

publishing {
    repositories {
        slneReleases()
    }
}