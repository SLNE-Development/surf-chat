package dev.slne.surf.chat.core.common.netty.packet.clientbound

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.ResponseNettyPacket
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("chat:clientbound:history_lookup_result", PacketFlow.CLIENTBOUND)
data class ClientboundHistoryLookupResultPacket(
    val entries: List<HistoryEntry>
) : ResponseNettyPacket()