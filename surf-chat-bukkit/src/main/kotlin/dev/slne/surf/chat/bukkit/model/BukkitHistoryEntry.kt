package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.HistoryEntryModel
import net.kyori.adventure.text.Component
import java.util.*

class BukkitHistoryEntry (
    override val message: String,
    override val timestamp: Long,
    override val uuid: UUID,
    override val type: String,
    override val id: UUID
): HistoryEntryModel