package dev.slne.surf.chat.core.paper

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import dev.slne.surf.redis.RedisApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path

class PaperLoader(
    dataPath: Path
) {
    val rabbitApi = ClientRabbitMQApi.create("surf-chat", dataPath)
    val redisApi = RedisApi.create()

    suspend fun onLoad() {
        rabbitApi.freezeAndConnect()

    }

    suspend fun onEnable() {
        withContext(Dispatchers.IO) {
            redisApi.freezeAndConnect()
        }
    }

    suspend fun onDisable() {
        rabbitApi.disconnect()
        withContext(Dispatchers.IO) {
            redisApi.disconnect()
        }
    }
}

val redisApi get() = PaperChatInstance.redisApi
val rabbiApi get() = PaperChatInstance.rabbitApi