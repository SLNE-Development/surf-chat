package dev.slne.surf.chat.velocity.handler

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier

import dev.slne.surf.chat.core.Constants

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class ServerRequestHandler {
    val channelServerRequest: MinecraftChannelIdentifier =
        MinecraftChannelIdentifier.from(Constants.CHANNEL_SERVER_REQUEST)
    val channelServerResponse: MinecraftChannelIdentifier =
        MinecraftChannelIdentifier.from(Constants.CHANNEL_SERVER_RESPONSE)

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        event.result = PluginMessageEvent.ForwardResult.handled()

        if (event.identifier != channelServerRequest) {
            return
        }

        if (event.source !is ServerConnection) {
            return
        }
        val connection = event.source as ServerConnection
        connection.sendPluginMessage(
            channelServerResponse,
            ByteArrayOutputStream().use { byteStream ->
                DataOutputStream(byteStream).use { out ->
                    out.writeUTF(connection.server.serverInfo.name)
                }
                byteStream.toByteArray()
            }
        )
    }
}