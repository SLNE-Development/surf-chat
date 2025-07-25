package dev.slne.surf.chat.bukkit.listener

import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.bukkit.message.MessageDataImpl
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.Constants
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
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
        if (channel != Constants.CHANEL_DM) {
            return
        }

        message.inputStream().use { byteSteam ->
            DataInputStream(byteSteam).use { input ->
                val senderUuid = UUID.fromString(input.readUTF())
                val targetUuid = UUID.fromString(input.readUTF())
                val messageUuid = UUID.fromString(input.readUTF())
                val message = input.readUTF()
                val sentAt = input.readLong()
                val serverName = input.readUTF()

                val sender = Bukkit.getPlayer(senderUuid)

                if (sender != null) {
                    val user = sender.user() ?: return
                    val target = Bukkit.getPlayer(targetUuid)

                    val messageData = MessageDataImpl(
                        Component.text(message),
                        user,
                        target?.user(),
                        sentAt,
                        messageUuid,
                        serverName,
                        null,
                        null,
                        MessageType.DIRECT
                    )
                }

                val target = Bukkit.getPlayer(targetUuid)

                if (sender != null) {
                    val user = sender.user() ?: return
                    val target = Bukkit.getPlayer(targetUuid)

                    val messageData = MessageDataImpl(
                        Component.text(message),
                        user,
                        target?.user(),
                        sentAt,
                        messageUuid,
                        serverName,
                        null,
                        null,
                        MessageType.DIRECT
                    )
                }
            }
        }
    }
}