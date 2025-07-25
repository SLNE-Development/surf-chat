package dev.slne.surf.chat.bukkit.pluginmessage

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.DataInputStream

object PluginMessageRegistry : PluginMessageListener {
    private val handlers = mutableMapOf<String, (DataInputStream, Player) -> Unit>()

    fun register(channel: String, handler: (DataInputStream, Player) -> Unit) {
        val plugin = Bukkit.getPluginManager().getPlugin("MyPlugin")!!

        if (!Bukkit.getMessenger().isIncomingChannelRegistered(plugin, channel)) {
            Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, this)
        }

        handlers[channel] = handler
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        val plugin = Bukkit.getPluginManager().getPlugin("MyPlugin")!!
        if (!plugin.isEnabled) return

        handlers[channel]?.let { handler ->
            ByteArrayInputStream(message).use { byteIn ->
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
