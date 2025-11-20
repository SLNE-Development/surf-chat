package dev.slne.surf.chat.paper

import dev.slne.surf.chat.paper.listener.AsyncChatListener
import dev.slne.surf.chat.paper.listener.ConnectListener
import dev.slne.surf.chat.paper.listener.DisconnectListener
import dev.slne.surf.surfapi.bukkit.api.event.register

object PaperListenerManager {
    fun registerBukkitListeners() {
        AsyncChatListener().register()
        DisconnectListener().register()
        ConnectListener().register()
    }
}