package dev.slne.surf.chat.core.common.netty.packet.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

typealias ChatUuid = @Serializable(with = ChatUuidSerializer::class) UUID

object ChatUuidSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("ChatUuid", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}