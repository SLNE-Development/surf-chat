package dev.slne.surf.chat.core.common.netty.packet.serverbound.message

import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("chat:serverbound:message_team", PacketFlow.SERVERBOUND)
class ServerboundTeamMessagePacket(
    val messageData: MessageData
) : NettyPacket()