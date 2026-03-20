package dev.slne.surf.chat.core.paper

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import dev.slne.surf.redis.RedisApi
import java.nio.file.Path

class PaperLoader(
    dataPath: Path
) {
    val rabbitApi = ClientRabbitMQApi.create("surf-chat", dataPath)
    val redisApi = RedisApi.create()

    suspend fun onLoad() {
        // Rabbit
        rabbitApi.freezeAndConnect()
    }

    suspend fun onEnable() {
    }

    suspend fun onDisable() {
        rabbitApi.disconnect()
    }
}

val redisApi get() = PaperChatInstance.redisApi
val rabbiApi get() = PaperChatInstance.rabbitApi