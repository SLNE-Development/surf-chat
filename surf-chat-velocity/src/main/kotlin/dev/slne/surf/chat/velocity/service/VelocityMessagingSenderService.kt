package dev.slne.surf.chat.velocity.service

import com.google.auto.service.AutoService
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
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.*
import kotlin.jvm.optionals.getOrNull

@AutoService(MessagingSenderService::class)
class VelocityMessagingSenderService() : MessagingSenderService, Services.Fallback {
    override fun loadServers() {
        /**
         * Backends only handle servers.
         */
    }

    override fun sendData(
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

            val data = ByteArrayOutputStream().use { byteStream ->
                DataOutputStream(byteStream).use { out ->
                    out.writeUTF(player)
                    out.writeUTF(target)
                    out.writeUTF(GsonComponentSerializer.gson().serialize(message))
                    out.writeUTF(gson.toJson(type))
                    out.writeUTF(messageID.toString())
                    out.writeUTF(channel)
                    out.writeUTF(gson.toJson(forwardingServers))
                }
                byteStream.toByteArray()
            }

            server.sendPluginMessage(messageChannel, data)
        }
    }


    override fun sendTeamChatMessage(player: Audience, message: Component) {
        /**
         * Team Chat messages are only sent by backends.
         */
    }
}