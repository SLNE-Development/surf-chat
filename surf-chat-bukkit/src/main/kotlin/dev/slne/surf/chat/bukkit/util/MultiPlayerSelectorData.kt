package dev.slne.surf.chat.bukkit.util

import org.bukkit.OfflinePlayer
import java.util.*

data class MultiPlayerSelectorData(
    val isWildcard: Boolean,
    val player: OfflinePlayer?
) {
    fun getString(): String {
        if (this.isWildcard) {
            return "all"
        }

        return this.player?.name ?: "all"
    }

    fun parse(): UUID? {
        if (this.isWildcard) {
            return null
        }

        return this.player?.uniqueId
    }
}
