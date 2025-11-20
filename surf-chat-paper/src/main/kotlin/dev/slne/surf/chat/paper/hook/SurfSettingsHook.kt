package dev.slne.surf.chat.paper.hook

import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager

object SurfSettingsHook {
    fun isEnabled() = pluginManager.isPluginEnabled("surf-settings-paper")
}