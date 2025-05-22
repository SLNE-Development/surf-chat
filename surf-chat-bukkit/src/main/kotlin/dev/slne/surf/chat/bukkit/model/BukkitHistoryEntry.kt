package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.api.type.MessageType
import java.util.*

class BukkitHistoryEntry(
    override val message: String,
    override val timestamp: Long,
    override val userUuid: UUID,
    override val type: MessageType,
    override val entryUuid: UUID,
    override val deletedBy: String? = null,

    override val server: String = "N/A"
) : HistoryEntry