package dev.slne.surf.chat.paper.redis.rpc

import dev.slne.surf.api.paper.nms.NmsUseWithCaution
import dev.slne.surf.api.paper.nms.bridges.data.chat.PlayerChatMessageMirror
import dev.slne.surf.api.paper.nms.bridges.data.chat.RemoteChatSessionData
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.redis.request.RedisRequest
import kotlinx.serialization.Serializable

@Serializable
@OptIn(NmsUseWithCaution::class)
data class SendDirectMessageRedisRequest(
    val messageData: MessageData,
    val senderSession: RemoteChatSessionData?,
    val message: PlayerChatMessageMirror
) : RedisRequest()