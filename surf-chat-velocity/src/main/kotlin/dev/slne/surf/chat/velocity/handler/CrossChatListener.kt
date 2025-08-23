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
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import java.util.*

class CrossChatListener : PacketListenerAbstract() {
    val globalIndexCache = mutableObject2ObjectMapOf<UUID, Int>()
    val indexCache = mutableObject2ObjectMapOf<UUID, Int>()

    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType != PacketType.Play.Server.CHAT_MESSAGE) {
            return
        }

        if (event.serverVersion.isOlderThan(ServerVersion.V_1_21_5)) {
            error("Unsupported server version for cross-chat feature: ${event.serverVersion}. Minimum supported version is 1.21.5.")
        }


        val packet = WrapperPlayServerChatMessage(event)
        val player = event.getPlayer<Player>()

        event.isCancelled = true

        plugin.proxy.allPlayers
            .forEach {
                val chatMessage = packet.message as? ChatMessage_v1_21_5
                    ?: error("Expected ChatMessage_v1_21_5, got ${packet.message.javaClass.name}")

                val newGlobalIndex = (globalIndexCache[it.uniqueId] ?: -1) + 1
                globalIndexCache[it.uniqueId] = newGlobalIndex
                chatMessage.globalIndex = newGlobalIndex

                val newIndex = (indexCache[it.uniqueId] ?: -1) + 1
                indexCache[it.uniqueId] = newIndex
                chatMessage.index = newIndex

                println("Relaying message from ${player.username} to other servers. index: ${chatMessage.index}, globalIndex: ${chatMessage.globalIndex}")

                it.sendPacketSilent(WrapperPlayServerChatMessage(chatMessage))
            }
    }
}