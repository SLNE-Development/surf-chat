package dev.slne.surf.chat.velocity.handler

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier

import dev.slne.surf.chat.core.Constants

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class ServerRequestHandler {
    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        event.result = PluginMessageEvent.ForwardResult.handled()

        if (event.identifier != MinecraftChannelIdentifier.from(Constants.CHANNEL_SERVER_REQUEST)) {
            return
        }

        if (event.source !is ServerConnection) {
            return
        }
        val connection = event.source as ServerConnection
        connection.sendPluginMessage(
            MinecraftChannelIdentifier.from(Constants.CHANNEL_SERVER_RESPONSE),
            ByteArrayOutputStream().use { byteStream ->
                DataOutputStream(byteStream).use { out ->
                    out.writeUTF(connection.server.serverInfo.name)
                }
                byteStream.toByteArray()
            }
        )
    }
}