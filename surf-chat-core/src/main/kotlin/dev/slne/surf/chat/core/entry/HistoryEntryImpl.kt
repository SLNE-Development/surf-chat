package dev.slne.surf.chat.core.entry

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.model.MessageType
import java.util.*

data class HistoryEntryImpl(
    override val messageUuid: UUID,
    override val senderUuid: UUID,
    override val messageType: MessageType,
    override val sentAt: Long,
    override val messageLike: String,
    override val server: String,
    override val deletedBy: String?
) : HistoryEntry

data class HistoryFilterImpl(
    override val messageUuid: UUID?,
    override val senderUuid: UUID?,
    override val messageType: MessageType?,
    override val range: Long?,
    override val messageLike: String?,
    override val server: String?,
    override val deletedBy: String?
) : HistoryFilter
