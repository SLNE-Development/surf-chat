package dev.slne.surf.chat.velocity.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.core.service.messaging.MessagingSenderService
import dev.slne.surf.chat.velocity.gson
import dev.slne.surf.chat.velocity.messageChannel
import dev.slne.surf.chat.velocity.plugin

import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.util.Services
import java.util.UUID

import kotlin.jvm.optionals.getOrNull

@AutoService(MessagingSenderService::class)
class VelocityMessagingSenderService(): MessagingSenderService, Services.Fallback {
    override fun loadServers() {
        /**
         * Backends only handle servers.
         */
    }

    override fun sendData (
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: UUID,
        channel: String,
        forwardingServers: ObjectSet<String>
    ) {
        for (backend in forwardingServers) {
            val server = plugin.proxy.getServer(backend).getOrNull() ?: continue

            server.sendPluginMessage(messageChannel, ByteStreams.newDataOutput().apply {
                writeUTF(player)
                writeUTF(target)
                writeUTF(GsonComponentSerializer.gson().serialize(message))
                writeUTF(gson.toJson(type))
                writeUTF(messageID.toString())
                writeUTF(channel)
                writeUTF(gson.toJson(forwardingServers))
            }.toByteArray())
        }
    }

    override fun sendTeamChatMessage(player: Audience, message: Component) {
        /**
         * Team Chat messages are only sent by backends.
         */
    }
}