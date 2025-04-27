package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams
import com.google.gson.reflect.TypeToken
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.util.history.LoggedMessage
import dev.slne.surf.chat.bukkit.util.gson
import dev.slne.surf.chat.bukkit.util.serverPlayers
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.messaging.MessagingReceiverService
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.util.UUID

@AutoService(MessagingReceiverService::class)
class BukkitMessagingReceiverService : MessagingReceiverService, PluginMessageListener, Fallback {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != SurfChatApi.messagingChannelIdentifier) return

        val input = ByteStreams.newDataInput(message)
        val sender = input.readUTF()
        val target = input.readUTF()
        val messageJson = input.readUTF()
        val typeJson = input.readUTF()
        val messageId = UUID.fromString(input.readUTF())
        val chatChannel = input.readUTF()
        val forwardingServers: Set<String> = gson.fromJson(input.readUTF(), object : TypeToken<Set<String>>() {}.type)

        val chatMessage = GsonComponentSerializer.gson().deserialize(messageJson)
        val messageType = gson.fromJson(typeJson, ChatMessageType::class.java)

        handleReceive(sender, target, chatMessage, messageType, messageId, chatChannel, forwardingServers.toObjectSet())
    }

    override fun handleReceive (
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: UUID,
        channel: String,
        forwardingServers: ObjectSet<String>
    ) {
        serverPlayers.forEach {
            historyService.logCaching(it.uniqueId, LoggedMessage(player, "Unknown", message), messageID)
        }

        Bukkit.broadcast(message)
    }
}
