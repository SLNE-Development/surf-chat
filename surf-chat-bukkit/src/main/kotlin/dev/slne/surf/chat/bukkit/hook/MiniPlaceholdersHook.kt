package dev.slne.surf.chat.bukkit.hook

import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object MiniPlaceholdersHook {
    private fun isEnabled() = Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")

    fun parseAudience(player: Player, input: String): Component {
        if (!this.isEnabled()) {
            return MiniMessage.miniMessage().deserialize(input)
        }

        val resolver = MiniPlaceholders.getAudienceGlobalPlaceholders(player)
        return MiniMessage.miniMessage().deserialize(input, player, resolver)
    }

    fun parseGlobal(input: String): Component {
        if (!this.isEnabled()) {
            return MiniMessage.miniMessage().deserialize(input)
        }

        val resolver = MiniPlaceholders.getGlobalPlaceholders()
        return MiniMessage.miniMessage().deserialize(input, resolver)
    }
}