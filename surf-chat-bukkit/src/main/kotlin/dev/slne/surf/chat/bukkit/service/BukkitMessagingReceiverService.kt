package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.gson
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.messaging.MessagingReceiverService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

@AutoService(MessagingReceiverService::class)
class BukkitMessagingReceiverService : MessagingReceiverService, PluginMessageListener, Fallback {
    init {
        plugin.server.messenger.registerIncomingPluginChannel(plugin, SurfChatApi.messagingChannelIdentifier, this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != SurfChatApi.messagingChannelIdentifier) return

        val input = ByteStreams.newDataInput(message)
        val sourceServer = input.readUTF()
        val forwardingServersJson = input.readUTF()
        val sender = input.readUTF()
        val target = input.readUTF()
        val messageJson = input.readUTF()
        val typeJson = input.readUTF()
        val messageId = input.readLong()
        val chatChannel = input.readUTF()

        val chatMessage = GsonComponentSerializer.gson().deserialize(messageJson)
        val messageType = gson.fromJson(typeJson, ChatMessageType::class.java)

        handleReceive(sourceServer, sender, target, chatMessage, messageType, messageId, chatChannel)
    }

    override fun handleReceive(
        server: String,
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: Long,
        channel: String
    ) {

    }
}
