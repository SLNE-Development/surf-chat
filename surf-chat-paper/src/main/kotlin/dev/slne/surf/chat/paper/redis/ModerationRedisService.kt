package dev.slne.surf.chat.paper.redis

import dev.slne.surf.api.core.util.logger
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.core.paper.redisApi
import dev.slne.surf.redis.codec.JsonKotlinCodec
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

object ModerationRedisService {
    private val log = logger()

    private val moderationCache by lazy {
        redisApi.redisson.getSetCache<ModerationCacheEntry>(
            "surf-chat:v3:moderations",
            JsonKotlinCodec.of<ModerationCacheEntry>()
        )
    }

    private val chatMessageCache by lazy {
        redisApi.redisson.getSetCache<MessageData>(
            "surf-chat:v3:messages",
            JsonKotlinCodec.of<MessageData>()
        )
    }

    fun cache(messageData: MessageData, classificationResult: ModerationClassificationResult) {
        moderationCache.addAsync(ModerationCacheEntry(messageData, classificationResult), 1, TimeUnit.HOURS)
            .exceptionally { throwable ->
                log.atWarning()
                    .withCause(throwable)
                    .log("Failed to cache moderation result for message ${messageData.messageUuid}")
                null
            }
    }

    fun cache(messageData: MessageData) {
        chatMessageCache.addAsync(messageData, 1, TimeUnit.HOURS).exceptionally { throwable ->
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