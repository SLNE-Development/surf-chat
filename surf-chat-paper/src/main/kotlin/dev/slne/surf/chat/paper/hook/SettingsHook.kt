package dev.slne.surf.chat.paper.hook

import dev.slne.surf.settings.api.surfSettingsApi
import org.bukkit.Bukkit
import java.util.*

object SettingsHook {
    fun isEnabled() = Bukkit.getPluginManager().isPluginEnabled("surf-settings-paper")

    fun hasDirectMessagesEnabled(playerUuid: UUID): Boolean {
        return if (isEnabled()) {
            surfSettingsApi.getPlayerSetting(playerUuid, "direct_messages")?.getBoolean()
                ?: true
        } else {
            true
        }
    }

    fun hasChatPingsEnabled(playerUuid: UUID): Boolean {
        return if (isEnabled()) {
            surfSettingsApi.getPlayerSetting(playerUuid, "chat_pings")?.getBoolean()
                ?: true
        } else {
            true
        }
    }
}