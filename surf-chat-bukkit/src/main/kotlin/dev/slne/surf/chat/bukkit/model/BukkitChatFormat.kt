package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.ChatFormat
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.api.user.ChatUser
import net.kyori.adventure.text.Component
import java.util.*

class BukkitChatFormat : ChatFormat {
    override fun formatMessage(
        rawMessage: Component,
        sender: ChatUser,
        viewer: ChatUser,
        messageType: MessageType,
        channel: String,
        messageID: UUID,
        warn: Boolean
    ): Component {
        when(messageType) {
            MessageType.GLOBAL -> TODO()
            MessageType.CHANNEL -> TODO()
            MessageType.PRIVATE_FROM -> TODO()
            MessageType.PRIVATE_TO -> TODO()
            MessageType.PRIVATE -> TODO()
            MessageType.TEAM -> TODO()
            MessageType.INTERNAL -> TODO()
            MessageType.REPLY -> TODO()
        }
}

    fun convertLegacy(input: String): String {
        val regex = Regex("&#[A-Fa-f0-9]{6}")
        return regex.replace(input) { matchResult ->
            val hex = matchResult.value.removePrefix("&#")
            "<#$hex>"
        }
    }
}