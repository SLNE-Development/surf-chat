package dev.slne.surf.chat.paper.redis

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.core.paper.redisApi
import kotlinx.serialization.Serializable

object ModerationRedisService {
    private val moderationCache =
        redisApi.redissonReactive.getSetCache<ModerationCacheEntry>("v3:moderations")
    private val chatMessageCache = redisApi.redissonReactive.getSetCache<MessageData>("v3:messages")

    fun init() = Unit

    fun cache(messageData: MessageData, classificationResult: ModerationClassificationResult) {
        moderationCache.add(ModerationCacheEntry(messageData, classificationResult))
    }

    fun cache(messageData: MessageData) {
        chatMessageCache.add(messageData)
    }

    @Serializable
    private data class ModerationCacheEntry(
        val messageData: MessageData,
        val classificationResult: ModerationClassificationResult
    )
}