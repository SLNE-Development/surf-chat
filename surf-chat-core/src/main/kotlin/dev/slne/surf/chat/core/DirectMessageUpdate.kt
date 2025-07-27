package dev.slne.surf.chat.core

import net.kyori.adventure.text.Component
import java.util.*

data class DirectMessageUpdate(
    val type: DirectMessageUpdateType,
    val senderUuid: UUID,
    val senderName: String,
    val targetUuid: UUID,
    val targetName: String,
    val messageUuid: UUID,
    val message: Component,
    val sentAt: Long,
    val serverName: String
)

enum class DirectMessageUpdateType {
    SEND_MESSAGE,
    RECEIVE_MESSAGE,
    LOG_MESSAGE,
    SEND_AND_LOG_MESSAGE,
}