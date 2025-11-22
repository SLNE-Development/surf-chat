package dev.slne.surf.chat.core.common.netty.packet.serverbound

import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.chat.SignedMessage
import java.util.*

@Serializable
@SurfNettyPacket("chat:serverbound:denylist_action", PacketFlow.SERVERBOUND)
data class ServerboundDenylistActionPacket(
    val messageId: @Contextual UUID,
    val denylistEntry: DenylistEntry,
    val signature: SignedMessage.Signature?,
    val player: CloudPlayer,
) : NettyPacket()