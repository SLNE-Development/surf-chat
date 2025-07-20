package dev.slne.surf.chat.bukkit.hook

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlaceholderAPIHook {
    fun isEnabled() = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
    fun parse(player: Player, input: String): String {
        if (!this.isEnabled()) {
            return input
        }

        return PlaceholderAPI.setPlaceholders(player, input)
    }

    fun parse(player: Player, input: Component): Component {
        return MiniMessage.miniMessage()
            .deserialize(this.parse(player, MiniMessage.miniMessage().serialize(input)))
    }
}