package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.gson
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.messaging.MessagingSenderService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback

@AutoService(MessagingSenderService::class)
class BukkitMessagingSenderService(): MessagingSenderService, Fallback {
    override fun sendData(
        server: String,
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: Long,
        channel: String
    ) {
        val out = ByteStreams.newDataOutput()

        out.writeUTF(server)
        out.writeUTF(player)
        out.writeUTF(target)
        out.writeUTF(gson.toJson(message))
        out.writeUTF(gson.toJson(type))
        out.writeLong(messageID)
        out.writeUTF(channel)

        plugin.server.sendPluginMessage(plugin, SurfChatApi.messagingChannelIdentifier, out.toByteArray())
    }
}