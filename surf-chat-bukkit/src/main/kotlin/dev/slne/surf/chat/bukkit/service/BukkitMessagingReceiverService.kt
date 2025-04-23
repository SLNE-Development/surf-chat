package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.util.history.LoggedMessage
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.gson
import dev.slne.surf.chat.bukkit.util.serverPlayers
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.messaging.MessagingReceiverService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.util.UUID

@AutoService(MessagingReceiverService::class)
class BukkitMessagingReceiverService : MessagingReceiverService, PluginMessageListener, Fallback {
    init {
        plugin.server.messenger.registerIncomingPluginChannel(plugin, SurfChatApi.messagingChannelIdentifier, this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != SurfChatApi.messagingChannelIdentifier) return

        val input = ByteStreams.newDataInput(message)
        val sender = input.readUTF()
        val target = input.readUTF()
        val messageJson = input.readUTF()
        val typeJson = input.readUTF()
        val messageId = UUID.nameUUIDFromBytes(input.readLong().toString().toByteArray())
        val chatChannel = input.readUTF()

        val chatMessage = GsonComponentSerializer.gson().deserialize(messageJson)
        val messageType = gson.fromJson(typeJson, ChatMessageType::class.java)

        handleReceive(sender, target, chatMessage, messageType, messageId, chatChannel)
    }

    override fun handleReceive (
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: UUID,
        channel: String
    ) {
        serverPlayers.forEach {
            historyService.logCaching(it.uniqueId, LoggedMessage(player, "Unknown", message), messageID)
        }

        Bukkit.broadcast(message)
    }
}
