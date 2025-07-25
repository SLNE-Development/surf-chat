package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.model.MessageType
import java.util.*

interface HistoryEntry {
    val messageUuid: UUID
    val senderUuid: UUID
    val receiverUuid: UUID?
    val messageType: MessageType
    val sentAt: Long
    val message: String
    val server: String
    val channel: String?
    val deletedBy: String?
}

interface HistoryFilter {
    val messageUuid: UUID?
    val senderUuid: UUID?
    val receiverUuid: UUID?
    val messageType: MessageType?
    val range: Long?
    val messageLike: String?
    val server: String?
    val channel: String?
    val deletedBy: String?
    val type: MessageType?
    val limit: Int?
}