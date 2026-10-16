import dev.slne.surf.api.gradle.util.slneReleases
import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.minestom")
    id("dev.slne.surf.microservice")
}

surfMinestomApi {
    withCoreMinestom()
    withSurfRedis()
}

minestomPluginFile {
    main = "dev.slne.surf.chat.minestom.SurfChatMinestomPlugin"
    authors = listOf("red")

    pluginDependencies {
        register("surf-rabbitmq-minestom")
        register("surf-redis-minestom")
        register("surf-settings-minestom")
    }
}

surfMicroservice {
    withRabbitModule(RabbitModule.CLIENT_API)
}

dependencies {
    api(projects.surfChatCore.surfChatCoreClient)
    compileOnly("dev.slne.surf.punish:surf-punish-api-common:+")
}

publishing {
    repositories {
        slneReleases()
    }
}
