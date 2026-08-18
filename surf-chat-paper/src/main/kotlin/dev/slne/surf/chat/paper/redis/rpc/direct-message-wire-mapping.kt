package dev.slne.surf.chat.paper.redis.rpc

import dev.slne.surf.api.paper.nms.NmsUseWithCaution
import dev.slne.surf.api.paper.nms.bridges.data.chat.PlayerChatMessageMirror
import dev.slne.surf.api.paper.nms.bridges.data.chat.RemoteChatSessionData
import dev.slne.surf.chat.core.client.redis.rpc.SignedChatMessage

@OptIn(NmsUseWithCaution::class)
fun PlayerChatMessageMirror.toWire(session: RemoteChatSessionData?) = SignedChatMessage(
    sender = link.sender,
    sessionId = link.sessionId,
    index = link.index,
    signature = signature,
    content = signedBody.content,
    timestamp = signedBody.timestamp,
    salt = signedBody.salt,
    lastSeen = signedBody.lastSeen.entries,
    unsignedContent = unsignedContent,
    sessionKey = session?.let {
        SignedChatMessage.SessionKey(it.expiresAt, it.key, it.keySignature)
    }
)

@OptIn(NmsUseWithCaution::class)
fun SignedChatMessage.toMirror() = PlayerChatMessageMirror(
    link = PlayerChatMessageMirror.SignedMessageLink(index, sender, sessionId),
    signature = signature,
    signedBody = PlayerChatMessageMirror.SignedMessageBody(
        content,
        timestamp,
        salt,
        PlayerChatMessageMirror.SignedMessageBody.LastSeenMessages(lastSeen)
    ),
    unsignedContent = unsignedContent,
    filterMask = PlayerChatMessageMirror.FilterMask.PASS_THROUGH
)

@OptIn(NmsUseWithCaution::class)
fun SignedChatMessage.chatSession() = sessionKey?.let {
    RemoteChatSessionData(sessionId, it.expiresAt, it.key, it.keySignature)
}
