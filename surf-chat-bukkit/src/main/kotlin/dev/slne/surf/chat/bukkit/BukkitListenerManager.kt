package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.listener.AsyncChatListener
import dev.slne.surf.chat.bukkit.listener.ConnectListener
import dev.slne.surf.chat.bukkit.listener.DisconnectListener
import dev.slne.surf.chat.bukkit.listener.ServerResponseListener
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.Bukkit

object BukkitListenerManager {
    fun registerBukkitListeners() {
        AsyncChatListener().register()
        DisconnectListener().register()
        ConnectListener().register()

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "surf-chat:server_request")
        Bukkit.getMessenger().registerIncomingPluginChannel(
            plugin, "surf-chat:server_response",
            ServerResponseListener()
        );
    }

    fun registerPacketListeners() {

    }
}