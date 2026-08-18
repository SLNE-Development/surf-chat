package dev.slne.surf.chat.minestom.redis.rpc

import dev.slne.minestom.lobby.api.chat.RemoteSignedMessage
import dev.slne.surf.chat.core.client.redis.rpc.SignedChatMessage
import net.minestom.server.crypto.ChatSession
import net.minestom.server.crypto.MessageSignature
import net.minestom.server.crypto.PlayerPublicKey

fun RemoteSignedMessage.toWire(session: ChatSession?) = SignedChatMessage(
    sender = sender,
    sessionId = sessionId,
    index = index,
    signature = signature?.signature(),
    content = content,
    timestamp = timestamp,
    salt = salt,
    lastSeen = lastSeen.map { it.signature() },
    unsignedContent = unsignedContent,
    sessionKey = session?.publicKey()?.let {
        SignedChatMessage.SessionKey(it.expiresAt(), it.publicKey(), it.signature())
    }
)

fun SignedChatMessage.toLobby() = RemoteSignedMessage(
    sender = sender,
    sessionId = sessionId,
    index = index,
    signature = signature?.let(::MessageSignature),
    content = content,
    timestamp = timestamp,
    salt = salt,
    lastSeen = lastSeen.map(::MessageSignature),
    unsignedContent = unsignedContent
)

fun SignedChatMessage.chatSession() = sessionKey?.let {
    ChatSession(sessionId, PlayerPublicKey(it.expiresAt, it.key, it.keySignature))
}
