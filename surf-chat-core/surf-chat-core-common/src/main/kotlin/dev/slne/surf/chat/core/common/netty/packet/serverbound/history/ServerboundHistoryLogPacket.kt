package dev.slne.surf.chat.core.common.netty.packet.serverbound.history

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable

@SurfNettyPacket("chat:serverbound:history_log", PacketFlow.SERVERBOUND)
@Serializable
class ServerboundHistoryLogPacket(
    val messageData: MessageData
) : NettyPacket()