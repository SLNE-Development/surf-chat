package dev.slne.surf.chat.core.common

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.rabbitmq.api.RabbitMQApi
import dev.slne.surf.redis.RedisApi

private val instance = requiredService<ChatInstance>()

interface ChatInstance {
    val rabbitApi: RabbitMQApi
    val redisApi: RedisApi

    companion object : ChatInstance by instance {
        val INSTANCE get() = instance
    }
}