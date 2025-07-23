package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.model.MessageType
import java.util.*

interface HistoryEntry {
    val entryUuid: UUID
    val senderUuid: UUID
    val messageType: MessageType
    val sentAt: Long
    val messageLike: String
    val server: String
    val deletedBy: String?
}

interface HistoryFilter {
    val messageUuid: UUID?
    val senderUuid: UUID?
    val messageType: MessageType?
    val range: Long?
    val messageLike: String?
    val server: String?
    val deletedBy: String?
}