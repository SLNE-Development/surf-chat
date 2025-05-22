package dev.slne.surf.chat.fallback.model.entry

import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.api.type.MessageType
import java.util.UUID

data class FallbackHistoryEntry(
    override val entryUuid: UUID,
    override val userUuid: UUID,
    override val type: MessageType,
    override val timestamp: Long,
    override val message: String,
    override val deletedBy: String?,
    override val server: String
) : HistoryEntry {
}