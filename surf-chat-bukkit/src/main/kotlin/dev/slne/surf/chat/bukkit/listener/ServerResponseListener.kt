package dev.slne.surf.chat.bukkit.listener

import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.Constants
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.DataInputStream
import java.util.*

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
                plugin.serverName = Optional.of(input.readUTF())
            }
        }
    }
}
