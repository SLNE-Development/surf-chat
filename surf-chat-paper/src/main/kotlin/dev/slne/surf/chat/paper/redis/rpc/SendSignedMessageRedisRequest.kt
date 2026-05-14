package dev.slne.surf.chat.paper.redis.rpc

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.api.paper.nms.NmsUseWithCaution
import dev.slne.surf.api.paper.nms.bridges.data.chat.PlayerChatMessageMirror
import dev.slne.surf.api.paper.nms.bridges.data.chat.RemoteChatSessionData
import dev.slne.surf.redis.request.RedisRequest
import kotlinx.serialization.Serializable

@Serializable
@OptIn(NmsUseWithCaution::class)
data class SendSignedMessageRedisRequest(
    val sender: SerializableUUID,
    val senderName: String,
    val target: SerializableUUID,
    val messageMirror: PlayerChatMessageMirror,
    val senderSession: RemoteChatSessionData?
) : RedisRequest()