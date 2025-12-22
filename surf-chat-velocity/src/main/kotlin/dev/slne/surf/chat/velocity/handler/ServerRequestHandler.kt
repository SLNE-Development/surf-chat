package dev.slne.surf.chat.velocity.handler

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class ServerRequestHandler {
    private val channelServerRequest: MinecraftChannelIdentifier =
        MinecraftChannelIdentifier.from("surf-chat:server_request")
    private val channelServerResponse: MinecraftChannelIdentifier =
        MinecraftChannelIdentifier.from("surf-chat:server_response")

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