import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.surfapi.gradle.core")
    id("dev.slne.surf.microservice")
}

dependencies {
    api(projects.surfChatApi)
}

surfMicroservice {
    withRabbitModule(RabbitModule.COMMON_API)
}