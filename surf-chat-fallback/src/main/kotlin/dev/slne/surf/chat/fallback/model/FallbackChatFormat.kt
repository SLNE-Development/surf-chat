package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.model.ChatFormat
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.type.MessageType
import net.kyori.adventure.text.Component
import java.util.UUID

class FallbackChatFormat : ChatFormat {
    override fun formatMessage(
        rawMessage: Component,
        sender: ChatUser,
        viewer: ChatUser,
        messageType: MessageType,
        channel: String,
        messageID: UUID,
        warn: Boolean
    ): Component {
        TODO("Not yet implemented")
    }
}