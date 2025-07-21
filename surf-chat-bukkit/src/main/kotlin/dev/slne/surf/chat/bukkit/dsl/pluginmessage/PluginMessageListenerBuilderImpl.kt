package dev.slne.surf.chat.bukkit.dsl.pluginmessage

import dev.slne.surf.chat.bukkit.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.DataInput
import java.io.DataInputStream


/**
 * Credits to ChatGPT
 */

class PluginMessageListenerBuilderImpl : PluginMessageListenerBuilder {

    private lateinit var channelName: String
    private var handler: ((Player, DataInput) -> Unit)? = null

    override fun channel(name: String): PluginMessageListenerBuilder = apply {
        this.channelName = name
    }

    override fun handler(block: (Player, DataInput) -> Unit): PluginMessageListenerBuilder = apply {
        this.handler = block
    }

    override fun register() {
        require(::channelName.isInitialized) { "Plugin channel must be set!" }
        requireNotNull(handler) { "Handler block must be set!" }

        val handler = this.handler!!

        Bukkit.getMessenger().registerIncomingPluginChannel(
            plugin, channelName,
            PluginMessageListener { channel, player, message ->
                if (channel != channelName) return@PluginMessageListener

                val input = DataInputStream(ByteArrayInputStream(message))
                handler(player, input)
            }
        )

        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, channelName)) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, channelName)
        }
    }
}
