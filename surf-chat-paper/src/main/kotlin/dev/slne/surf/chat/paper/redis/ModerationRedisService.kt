package dev.slne.surf.chat.paper.redis

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.core.paper.redisApi
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.minutes

object ModerationRedisService {
    private val moderationCache =
        redisApi.createSimpleSetRedisCache<ModerationCacheEntry>("v2:moderations", 30.minutes, {
            it.messageData.messageUuid.toString()
        })
    private val chatMessageCache = redisApi.createSimpleSetRedisCache<MessageData>("v2:messages", 30.minutes, {
        it.messageUuid.toString()
    })

    fun init() = Unit

    suspend fun cache(messageData: MessageData, classificationResult: ModerationClassificationResult) {
        moderationCache.add(ModerationCacheEntry(messageData, classificationResult))
    }

    suspend fun cache(messageData: MessageData) {
        chatMessageCache.add(messageData)
    }

    @Serializable
    private data class ModerationCacheEntry(
        val messageData: MessageData,
        val classificationResult: ModerationClassificationResult
    )
}