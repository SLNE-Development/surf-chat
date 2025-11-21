package dev.slne.surf.chat.core.common.netty.packet.serverbound.history

import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.core.common.netty.packet.clientbound.history.ClientboundHistoryLookupResultPacket
import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.RespondingNettyPacket

@SurfNettyPacket("chat:serverbound:history_lookup", PacketFlow.SERVERBOUND)
class ServerboundHistoryLookupPacket(
    val filter: HistoryFilter?
) : RespondingNettyPacket<ClientboundHistoryLookupResultPacket>()