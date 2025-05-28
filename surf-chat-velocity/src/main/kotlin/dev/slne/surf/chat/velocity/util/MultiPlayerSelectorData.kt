package dev.slne.surf.chat.velocity.util

import java.util.*

data class MultiPlayerSelectorData (
    val isWildcard: Boolean,
    val player: UUID
) {
    fun parse(): UUID? {
        if (this.isWildcard) {
            return null
        }

        return this.player
    }

    override fun toString(): String {
        if (this.isWildcard) {
            return "all"
        }

        return this.player.toString()
    }
}
