package dev.slne.surf.chat.core.common.netty.packet.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerializableRegex = @Serializable(with = RegexSerializer::class) Regex

class RegexSerializer : KSerializer<Regex> {
    override val descriptor = PrimitiveSerialDescriptor("Regex", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Regex) = encoder.encodeString(value.pattern)
    override fun deserialize(decoder: Decoder) = Regex(decoder.decodeString())
}