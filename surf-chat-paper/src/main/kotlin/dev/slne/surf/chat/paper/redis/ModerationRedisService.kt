package dev.slne.surf.chat.paper.redis

import dev.slne.surf.api.core.util.logger
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.core.paper.redisApi
import dev.slne.surf.redis.codec.JsonKotlinCodec
import kotlinx.serialization.Serializable

object ModerationRedisService {
    private val log = logger()

    private val moderationCache = redisApi.redisson.getSetCache<ModerationCacheEntry>(
        "v3:moderations",
        JsonKotlinCodec.of<ModerationCacheEntry>()
    )

    private val chatMessageCache = redisApi.redisson.getSetCache<MessageData>(
        "v3:messages",
        JsonKotlinCodec.of<MessageData>()
    )

    fun init() = Unit

    fun cache(messageData: MessageData, classificationResult: ModerationClassificationResult) {
        moderationCache.addAsync(ModerationCacheEntry(messageData, classificationResult)).exceptionally { throwable ->
            log.atWarning()
                .withCause(throwable)
                .log("Failed to cache moderation result for message ${messageData.messageUuid}")
            null
        }
    }

    fun cache(messageData: MessageData) {
        chatMessageCache.addAsync(messageData).exceptionally { throwable ->
            log.atWarning()
                .withCause(throwable)
                .log("Failed to cache message ${messageData.messageUuid}")
            null
        }
    }

    @Serializable
    private data class ModerationCacheEntry(
        val messageData: MessageData,
        val classificationResult: ModerationClassificationResult
    )
}