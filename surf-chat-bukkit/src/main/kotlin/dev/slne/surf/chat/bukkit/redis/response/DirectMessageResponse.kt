package dev.slne.surf.chat.bukkit.redis.response

import dev.slne.surf.redis.request.RedisResponse
import kotlinx.serialization.Serializable

@Serializable
data class DirectMessageResponse(
    val success: DirectMessageStatus
) : RedisResponse() {
    @Serializable
    enum class DirectMessageStatus {
        SUCCESS,
        USER_NOT_FOUND,
        DIRECT_MESSAGES_DISABLED
    }
}