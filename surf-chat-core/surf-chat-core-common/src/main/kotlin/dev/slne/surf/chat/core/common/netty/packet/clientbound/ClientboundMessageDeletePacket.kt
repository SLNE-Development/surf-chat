package dev.slne.surf.chat.core.common.netty.packet.clientbound

import dev.slne.surf.chat.core.common.netty.packet.serializer.SignedMessageSignatureSerializer
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable
import net.kyori.adventure.chat.SignedMessage

@Serializable
@SurfNettyPacket("chat:clientbound:message_delete", PacketFlow.CLIENTBOUND)
data class ClientboundMessageDeletePacket(
    @Serializable(with = SignedMessageSignatureSerializer::class)
    val signature: SignedMessage.Signature
) : NettyPacket()