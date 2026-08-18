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

dependencies {
    api(projects.surfChatApi)
}

surfMicroservice {
    withRabbitModule(RabbitModule.COMMON_API)
}

publishing {
    repositories {
        slneReleases()
    }
}