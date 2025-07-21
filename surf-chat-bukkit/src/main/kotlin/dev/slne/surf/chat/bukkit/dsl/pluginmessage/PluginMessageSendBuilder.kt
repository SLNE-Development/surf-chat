package dev.slne.surf.chat.bukkit.dsl.pluginmessage

import org.bukkit.entity.Player


/**
 * Credits to ChatGPT
 */

interface PluginMessageSendBuilder {
    companion object {
        fun builder(): PluginMessageSendBuilder = PluginMessageSendBuilderImpl()
        operator fun invoke() = builder()
        operator fun invoke(block: PluginMessageSendBuilder.() -> Unit) =
            builder().apply(block).send()
    }

    fun channel(name: String): PluginMessageSendBuilder
    fun target(player: Player): PluginMessageSendBuilder
    fun writeByte(value: Byte): PluginMessageSendBuilder
    fun writeInt(value: Int): PluginMessageSendBuilder
    fun writeUTF(value: String): PluginMessageSendBuilder
    fun writeBoolean(value: Boolean): PluginMessageSendBuilder
    fun writeBytes(bytes: ByteArray): PluginMessageSendBuilder

    fun send()
}
