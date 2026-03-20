package dev.slne.surf.chat.core.common

import dev.slne.surf.rabbitmq.api.RabbitMQApi
import dev.slne.surf.surfapi.core.api.util.requiredService

private val instance = requiredService<ChatInstance>()

interface ChatInstance {
    val rabbitApi: RabbitMQApi

    companion object : ChatInstance by instance {
        val INSTANCE get() = instance
    }
}