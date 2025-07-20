package dev.slne.surf.chat.bukkit.hook

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class PlaceholderAPIHook {
    fun isEnabled() = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
    fun parse(input: String): String {
        if(!this.isEnabled()) {
            return input
        }

        return
    }
    fun parse(input: Component): Component {
        if(!this.isEnabled()) {
            return input
        }

        return
    }
}