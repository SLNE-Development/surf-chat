package dev.slne.surf.chat.bukkit.dsl.pluginmessage

import org.bukkit.entity.Player
import java.io.DataInput

/**
 * Credits to ChatGPT
 */

interface PluginMessageListenerBuilder {
    companion object {
        fun builder(): PluginMessageListenerBuilder = PluginMessageListenerBuilderImpl()
        operator fun invoke() = builder()
        operator fun invoke(block: PluginMessageListenerBuilder.() -> Unit) =
            builder().apply(block).register()
    }

    fun channel(name: String): PluginMessageListenerBuilder
    fun handler(block: (player: Player, input: DataInput) -> Unit): PluginMessageListenerBuilder

    fun register()
}
