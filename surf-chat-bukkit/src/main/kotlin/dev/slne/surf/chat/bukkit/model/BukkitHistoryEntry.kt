package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.HistoryEntryModel
import net.kyori.adventure.text.Component
import java.util.*

class BukkitHistoryEntry (
    override val id: UUID,
    override val user: UUID,
    override val userName: String,
    override val message: Component,
    override val timestamp: Long
): HistoryEntryModel {
}