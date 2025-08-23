package dev.slne.surf.chat.velocity.handler

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_21_5
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.chat.velocity.util.sendPacketSilent
import kotlin.jvm.optionals.getOrNull

class CrossChatListener : PacketListenerAbstract() {

    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType != PacketType.Play.Server.CHAT_MESSAGE) return

        if (event.serverVersion.isOlderThan(ServerVersion.V_1_21_5)) {
            error("Unsupported server version for cross-chat feature: ${event.serverVersion}. Minimum supported version is 1.21.5.")
        }

        val wrapper = WrapperPlayServerChatMessage(event)
        val player = event.getPlayer<Player>()

        val original = wrapper.message as? ChatMessage_v1_21_5
            ?: return

        val clone = ChatMessage_v1_21_5(
            original.globalIndex,
            original.senderUUID,
            original.index,
            original.signature,
            original.plainContent,
            original.timestamp,
            original.salt,
            original.lastSeenMessagesPacked,
            original.unsignedChatContent.getOrNull(),
            original.filterMask,
            original.chatFormatting
        )

        println("Sending cloned chat message to original user}")
        println("index: ${clone.index}, globalIndex: ${clone.globalIndex}, signature: ${clone.signature}, content: ${clone.plainContent}, timestamp: ${clone.timestamp}, salt: ${clone.salt}")

        plugin.proxy.allPlayers
            .filter { it.uniqueId != player.uniqueId }
            .forEach {
                println("Sending cloned chat message to ${it.username}")
                println("index: ${clone.index}, globalIndex: ${clone.globalIndex}, signature: ${clone.signature}, content: ${clone.plainContent}, timestamp: ${clone.timestamp}, salt: ${clone.salt}")

                it.sendPacketSilent(WrapperPlayServerChatMessage(clone))
            }
    }
}
