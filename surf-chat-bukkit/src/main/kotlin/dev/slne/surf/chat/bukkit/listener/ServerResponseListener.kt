package dev.slne.surf.chat.bukkit.listener

import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.config.CONFIG_DISPLAY_DEFAULT
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.Constants
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.DataInputStream

class ServerResponseListener : PluginMessageListener {
    override fun onPluginMessageReceived(
        channel: String,
        player: Player,
        message: ByteArray
    ) {
        if (channel != Constants.CHANNEL_SERVER_RESPONSE) {
            return
        }

        message.inputStream().use { byteSteam ->
            DataInputStream(byteSteam).use { input ->
                val received = input.readUTF()
                plugin.chatServerConfig.edit {
                    internalName = received

                    if (displayName == CONFIG_DISPLAY_DEFAULT) {
                        displayName = received.replaceFirstChar { it.uppercase() }
                    }
                }
                plugin.server = ChatServer.of(
                    plugin.chatServerConfig.config.displayName,
                    plugin.chatServerConfig.config.internalName
                )
            }
        }
    }
}
