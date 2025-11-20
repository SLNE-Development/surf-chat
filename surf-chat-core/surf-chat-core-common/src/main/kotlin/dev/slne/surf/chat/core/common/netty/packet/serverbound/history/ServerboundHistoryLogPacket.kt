package dev.slne.surf.chat.core.common.netty.packet.serverbound.history

import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket

@SurfNettyPacket("chat:serverbound:history_log", PacketFlow.SERVERBOUND)
class ServerboundHistoryLogPacket(
    val messageData: MessageData
) : NettyPacket()