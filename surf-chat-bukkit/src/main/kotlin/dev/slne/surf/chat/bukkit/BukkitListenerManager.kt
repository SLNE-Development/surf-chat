package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.listener.*
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.Bukkit

object BukkitListenerManager {
    fun registerBukkitListeners() {
        AsyncChatListener().register()
        DisconnectListener().register()
        ConnectListener().register()

        Bukkit.getMessenger().registerIncomingPluginChannel(
            plugin, Constants.CHANNEL_DM,
            DirectMessageListener()
        )
        Bukkit.getMessenger().registerIncomingPluginChannel(
            plugin, Constants.CHANNEL_SERVER_RESPONSE,
            ServerResponseListener()
        );
    }

    fun registerPacketListeners() {

    }
}