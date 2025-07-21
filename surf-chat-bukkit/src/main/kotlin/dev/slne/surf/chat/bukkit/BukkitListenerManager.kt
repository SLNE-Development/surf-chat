package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.listener.AsyncChatListener
import dev.slne.surf.surfapi.bukkit.api.event.register

object BukkitListenerManager {
    fun registerBukkitListeners() {
        AsyncChatListener().register()
    }

    fun registerPacketListeners() {

    }
}