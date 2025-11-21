package dev.slne.surf.chat.core.common.netty.packet.serverbound.history

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@SurfNettyPacket("chat:serverbound:history_deleted", PacketFlow.SERVERBOUND)
@Serializable
class ServerboundHistoryMarkDeletedPacket(
    val messageUuid: @Contextual UUID,
    val deletedBy: String
) : NettyPacket()