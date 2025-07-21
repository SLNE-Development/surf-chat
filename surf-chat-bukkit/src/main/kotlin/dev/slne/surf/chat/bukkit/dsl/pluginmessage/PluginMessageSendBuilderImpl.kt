package dev.slne.surf.chat.bukkit.dsl.pluginmessage

import dev.slne.surf.chat.bukkit.plugin
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream


/**
 * Credits to ChatGPT
 */

class PluginMessageSendBuilderImpl : PluginMessageSendBuilder {

    private lateinit var channelName: String
    private lateinit var player: Player
    private val output = ByteArrayOutputStream()
    private val dataOutput = DataOutputStream(output)

    override fun channel(name: String): PluginMessageSendBuilder = apply {
        this.channelName = name
    }

    override fun target(player: Player): PluginMessageSendBuilder = apply {
        this.player = player
    }

    override fun writeByte(value: Byte): PluginMessageSendBuilder = apply {
        dataOutput.writeByte(value.toInt())
    }

    override fun writeInt(value: Int): PluginMessageSendBuilder = apply {
        dataOutput.writeInt(value)
    }

    override fun writeUTF(value: String): PluginMessageSendBuilder = apply {
        dataOutput.writeUTF(value)
    }

    override fun writeBoolean(value: Boolean): PluginMessageSendBuilder = apply {
        dataOutput.writeBoolean(value)
    }

    override fun writeBytes(bytes: ByteArray): PluginMessageSendBuilder = apply {
        dataOutput.write(bytes)
    }

    override fun send() {
        require(::channelName.isInitialized) { "Plugin channel must be set!" }
        require(::player.isInitialized) { "Target player must be set!" }

        val bytes = output.toByteArray()
        player.sendPluginMessage(plugin, channelName, bytes)
    }
}
