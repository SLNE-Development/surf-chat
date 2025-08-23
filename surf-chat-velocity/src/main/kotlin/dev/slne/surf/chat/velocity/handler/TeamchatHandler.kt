package dev.slne.surf.chat.velocity.handler

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import java.io.DataInputStream

class TeamchatHandler {
    private val channelTeam: MinecraftChannelIdentifier =
        MinecraftChannelIdentifier.from(Constants.CHANNEL_TEAM)

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        event.result = PluginMessageEvent.ForwardResult.handled()

        if (event.identifier != channelTeam) {
            return
        }

        event.data.inputStream().use { byteStream ->
            DataInputStream(byteStream).use { input ->
                val message = GsonComponentSerializer.gson().deserialize(input.readUTF())
                plugin.proxy.allPlayers
                    .filter { it.hasPermission(Constants.TEAM_PERMISSION) }
                    .forEach { it.sendText { append(message) } }
            }
        }
    }
}