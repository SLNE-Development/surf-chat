package dev.slne.surf.chat.bukkit.pluginmessage

import dev.slne.surf.chat.bukkit.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.DataInputStream

object PluginMessageRegistry : PluginMessageListener {
    private val handlers = mutableMapOf<String, (DataInputStream, Player) -> Unit>()

    fun register(channel: String, handler: (DataInputStream, Player) -> Unit) {
        if (!Bukkit.getMessenger().isIncomingChannelRegistered(plugin, channel)) {
            Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, this)
        }

        handlers[channel] = handler
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        handlers[channel]?.let { handler ->
            message.inputStream().use { byteIn ->
                DataInputStream(byteIn).use { dataIn ->
                    handler(dataIn, player)
                }
            }
        }
    }
}

fun pluginMessageListener(channel: String, handler: (DataInputStream, Player) -> Unit) {
    PluginMessageRegistry.register(channel, handler)
}
