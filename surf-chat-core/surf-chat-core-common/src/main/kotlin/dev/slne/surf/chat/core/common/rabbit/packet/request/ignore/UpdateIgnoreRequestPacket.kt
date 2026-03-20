package dev.slne.surf.chat.core.common.rabbit.packet.request.ignore

import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class UpdateIgnoreRequestPacket(
    val playerUuid: SerializableUUID,
    val targetPlayerUuid: SerializableUUID,
    val ignored: Boolean
)
