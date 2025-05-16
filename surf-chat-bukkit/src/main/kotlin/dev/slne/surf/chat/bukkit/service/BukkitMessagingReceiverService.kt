package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams
import com.google.gson.reflect.TypeToken
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.util.utils.gson
import dev.slne.surf.chat.core.service.messaging.MessagingReceiverService
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.util.*

@AutoService(MessagingReceiverService::class)
class BukkitMessagingReceiverService : MessagingReceiverService, PluginMessageListener, Fallback {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != SurfChatApi.MESSAGING_CHANNEL_IDENTIFIER) return

        val input = ByteStreams.newDataInput(message)
        val sender = input.readUTF()
        val target = input.readUTF()
        val message = GsonComponentSerializer.gson().deserialize(input.readUTF())
        val type = gson.fromJson(input.readUTF(), ChatMessageType::class.java)
        val messageId = UUID.fromString(input.readUTF())
        val chatChannel = input.readUTF()
        val forwardingServers: Set<String> =
            gson.fromJson(input.readUTF(), object : TypeToken<Set<String>>() {}.type)

        handleReceive(
            sender,
            target,
            message,
            type,
            messageId,
            chatChannel,
            forwardingServers.toObjectSet()
        )
    }

    override fun handleReceive(
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: UUID,
        channel: String,
        forwardingServers: ObjectSet<String>
    ) {
        Bukkit.broadcast(message)
    }

    override fun handeTeamChatReceive(message: Component) {
        /**
         * Team Chat messages are only sent by backends.
         */
    }
}
