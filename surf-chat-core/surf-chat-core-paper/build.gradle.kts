import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.paper-raw")
    id("dev.slne.surf.microservice")
}

surfRawPaperApi {
    withCoreCommon()
    withSurfRedis()
}

surfMicroservice {
    withRabbitModule(RabbitModule.CLIENT_API)
}

dependencies {
    api(projects.surfChatCore.surfChatCoreCommon)
}