package dev.slne.surf.chat.core.common.netty.packet.serverbound

import dev.slne.surf.chat.api.message.SignedMessageSignatureSerializer
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable
import net.kyori.adventure.chat.SignedMessage

@Serializable
@SurfNettyPacket("chat:serverbound:message_delete", PacketFlow.SERVERBOUND)
data class ServerboundMessageDeletePacket(
    @Serializable(with = SignedMessageSignatureSerializer::class)
    val signature: SignedMessage.Signature
) : NettyPacket()