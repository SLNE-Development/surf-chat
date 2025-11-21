package dev.slne.surf.chat.server.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import org.springframework.stereotype.Component

@Component
class ServerChatPacketListener {
    @SurfNettyPacketHandler

}