package dev.slne.surf.chat.core.common.netty.packet.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("chat:serverbound:reload", PacketFlow.SERVERBOUND)
class ServerboundReloadPacket : NettyPacket()