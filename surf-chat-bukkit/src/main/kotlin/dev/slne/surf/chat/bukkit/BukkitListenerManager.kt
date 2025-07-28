package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.listener.AsyncChatListener
import dev.slne.surf.chat.bukkit.listener.DirectMessageListener
import dev.slne.surf.chat.bukkit.listener.DisconnectListener
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.Bukkit

object BukkitListenerManager {
    fun registerBukkitListeners() {
        AsyncChatListener().register()
        DisconnectListener().register()

        Bukkit.getMessenger().registerIncomingPluginChannel(
            plugin, Constants.CHANNEL_DM,
            DirectMessageListener()
        )

    }

    fun registerPacketListeners() {

    }
}