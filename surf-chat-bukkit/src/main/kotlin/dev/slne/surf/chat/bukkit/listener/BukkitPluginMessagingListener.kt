package dev.slne.surf.chat.bukkit.listener

import com.google.common.io.ByteStreams
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.gson
import dev.slne.surf.chat.core.service.messaging.messagingReceiverService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class BukkitPluginMessagingListener(): PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if(channel != SurfChatApi.messagingChannelIdentifier) {
            return
        }

        val input = ByteStreams.newDataInput(message)

        val server = input.readUTF()
        val playerName = input.readUTF()
        val targetName = input.readUTF()
        val content = gson.fromJson(input.readUTF(), Component::class.java)
        val messageType = gson.fromJson(input.readUTF(), ChatMessageType::class.java)
        val messageID = input.readLong()
        val textChannel = input.readUTF()

        messagingReceiverService.handleReceive(server, playerName, targetName, content, messageType, messageID, textChannel)
    }
}