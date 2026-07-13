package dev.slne.surf.chat.paper.redis

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.core.paper.redisApi
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.minutes

object ModerationRedisService {
    private val moderationCache = redisApi.createSyncList<ModerationCacheEntry>("v1_moderations", ttl = 30.minutes)
    private val chatMessageCache = redisApi.createSyncList<MessageData>("v1_messages", ttl = 30.minutes)

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