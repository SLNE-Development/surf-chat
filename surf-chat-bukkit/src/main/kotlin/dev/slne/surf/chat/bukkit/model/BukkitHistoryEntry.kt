package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.api.type.ChatMessageType
import java.util.*

class BukkitHistoryEntry(
    override val message: String,
    override val timestamp: Long,
    override val userUuid: UUID,
    override val type: ChatMessageType,
    override val entryUuid: UUID,
    override val deleted: Boolean = false,
    override val deletedBy: String = "Not Deleted.",

    override val server: String = "N/A"
) : HistoryEntryModel