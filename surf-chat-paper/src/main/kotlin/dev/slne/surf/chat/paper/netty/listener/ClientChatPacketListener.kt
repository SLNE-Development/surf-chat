package dev.slne.surf.chat.paper.netty.listener

import dev.slne.surf.chat.core.common.netty.packet.clientbound.ClientboundMessageDeletePacket
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import org.bukkit.Bukkit
import org.springframework.stereotype.Component

@Component
class ClientChatPacketListener {
    @SurfNettyPacketHandler
    fun handleMessageDeletePacket(packet: ClientboundMessageDeletePacket) {
        Bukkit.getServer().deleteMessage(packet.signature)
    }
}