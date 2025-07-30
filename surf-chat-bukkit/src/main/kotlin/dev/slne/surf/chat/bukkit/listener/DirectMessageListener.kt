package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.bukkit.message.MessageDataImpl
import dev.slne.surf.chat.bukkit.message.MessageFormatterImpl
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.player
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.chat.core.DirectMessageUpdateType
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.DataInputStream
import java.util.*

class DirectMessageListener : PluginMessageListener {
    override fun onPluginMessageReceived(
        channel: String,
        player: Player,
        message: ByteArray
    ) {
        if (channel != Constants.CHANNEL_DM) {
            return
        }

        message.inputStream().use { byteSteam ->
            DataInputStream(byteSteam).use { input ->
                val type = DirectMessageUpdateType.valueOf(input.readUTF())
                val senderUuid = UUID.fromString(input.readUTF())
                val senderName = input.readUTF()
                val targetUuid = UUID.fromString(input.readUTF())
                val targetName = input.readUTF()
                val messageUuid = UUID.fromString(input.readUTF())
                val messageContent = input.readUTF()
                val sentAt = input.readLong()
                val serverName = input.readUTF()

                val messageData = MessageDataImpl(
                    Component.text(messageContent),
                    userService.getOfflineUser(senderUuid, senderName),
                    userService.getOfflineUser(targetUuid, targetName),
                    sentAt,
                    messageUuid,
                    serverName,
                    null,
                    null,
                    MessageType.DIRECT
                )

                when (type) {
                    DirectMessageUpdateType.SEND_MESSAGE -> handleSendMessage(messageData)
                    DirectMessageUpdateType.RECEIVE_MESSAGE -> handleReceiveMessage(
                        messageData
                    )

                    DirectMessageUpdateType.LOG_MESSAGE -> handleLogMessage(messageData)
                    DirectMessageUpdateType.SEND_AND_LOG_MESSAGE -> {
                        handleSendMessage(messageData)
                        handleLogMessage(messageData)
                    }
                }
            }
        }
    }

    private fun handleSendMessage(
        messageData: MessageData
    ) {
        val formatter = MessageFormatterImpl(messageData.message)
        val sender = messageData.sender.player() ?: return

        sender.sendText {
            append(formatter.formatOutgoingPm(messageData))
        }
    }

    private fun handleReceiveMessage(
        messageData: MessageData
    ) {
        val formatter = MessageFormatterImpl(messageData.message)
        val receiver = messageData.receiver ?: return


        plugin.launch(Dispatchers.IO) {
            if (receiver.configure().directMessagesEnabled()) {
                receiver.sendText {
                    append(formatter.formatIncomingPm(messageData))
                }
            }
        }
    }

    private fun handleLogMessage(
        messageData: MessageData
    ) {
        plugin.launch {
            historyService.logMessage(messageData)
        }
    }
}
