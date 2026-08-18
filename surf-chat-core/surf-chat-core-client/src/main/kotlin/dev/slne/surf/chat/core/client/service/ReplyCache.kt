package dev.slne.surf.chat.core.client.service

import dev.slne.surf.chat.core.client.redisApi
import dev.slne.surf.redis.codec.UUIDCodec
import dev.slne.surf.redis.libs.redisson.api.RMapCacheReactive
import kotlinx.coroutines.reactor.awaitSingleOrNull
import java.util.UUID
import java.util.concurrent.TimeUnit

object ReplyCache {
    private val lastMessages: RMapCacheReactive<UUID, UUID> by lazy {
        redisApi.redissonReactive.getMapCache("surf-chat:last-reply-cache", UUIDCodec.INSTANCE)
    }

    suspend fun getLastTarget(uuid: UUID) = lastMessages
        .get(uuid)
        .awaitSingleOrNull()

    suspend fun setLastTarget(uuid: UUID, target: UUID) = lastMessages
        .put(uuid, target, 15, TimeUnit.MINUTES)
        .awaitSingleOrNull()
}
