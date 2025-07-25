package dev.slne.surf.chat.bukkit.pluginmessage

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class PluginMessageSender(private val channel: String) {
    fun send(player: Player, build: DataOutputStream.() -> Unit) {
        val plugin = Bukkit.getPluginManager().getPlugin("MyPlugin")!!

        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, channel)) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, channel)
        }

        ByteArrayOutputStream().use { byteStream ->
            DataOutputStream(byteStream).use { dataOut ->
                dataOut.build()
                player.sendPluginMessage(plugin, channel, byteStream.toByteArray())
            }
        }
    }
}

fun pluginMessageSender(channel: String, player: Player, build: DataOutputStream.() -> Unit) {
    PluginMessageSender(channel).send(player, build)
}
