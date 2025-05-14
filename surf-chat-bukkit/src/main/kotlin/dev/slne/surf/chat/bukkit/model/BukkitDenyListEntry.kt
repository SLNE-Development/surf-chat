package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.DenyListEntry

data class BukkitDenyListEntry(
    override val word: String,
    override val reason: String,
    override val addedAt: Long,
    override val addedBy: String
) : DenyListEntry