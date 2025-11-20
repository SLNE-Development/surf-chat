package dev.slne.surf.chat.core.common.netty.packet.serverbound.history

import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket

@SurfNettyPacket("chat:serverbound:history_deleted", PacketFlow.SERVERBOUND)
class ServerboundHistoryMarkDeletedPacket(
    val messageData: MessageData
) : NettyPacket()