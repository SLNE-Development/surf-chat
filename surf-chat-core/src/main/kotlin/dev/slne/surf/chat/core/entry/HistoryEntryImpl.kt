package dev.slne.surf.chat.core.entry

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import java.util.*

data class HistoryEntryImpl(
    override val messageUuid: UUID,
    override val senderUuid: UUID,
    override val messageType: MessageType,
    override val sentAt: Long,
    override val message: String,
    override val server: String,
    override val deletedBy: String?,
    override val receiverUuid: UUID?,
    override val channel: String?
) : HistoryEntry

data class HistoryFilterImpl(
    override val messageUuid: UUID?,
    override val senderUuid: UUID?,
    override val messageType: MessageType?,
    override val range: Long?,
    override val messageLike: String?,
    override val server: String?,
    override val deletedBy: String?,
    override val receiverUuid: UUID?,
    override val channel: String?,
    override val type: MessageType?,
    override val limit: Int?
) : HistoryFilter
