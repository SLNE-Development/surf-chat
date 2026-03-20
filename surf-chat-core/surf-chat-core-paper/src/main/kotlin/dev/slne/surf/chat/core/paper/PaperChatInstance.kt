package dev.slne.surf.chat.core.paper

import dev.slne.surf.chat.core.common.ChatInstance
import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import dev.slne.surf.redis.RedisApi

interface PaperChatInstance : ChatInstance {
    val paperLoader: PaperLoader

    override val rabbitApi: ClientRabbitMQApi get() = paperLoader.rabbitApi
    override val redisApi: RedisApi get() = paperLoader.redisApi

    companion object : PaperChatInstance by ChatInstance.INSTANCE as PaperChatInstance {
        val INSTANCE get() = ChatInstance.INSTANCE
    }
}