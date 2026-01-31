package dev.slne.surf.chat.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.chat.SignedMessage


typealias SerializableSignature = @Serializable(with = SignedMessageSignatureSerializer::class) SignedMessage.Signature

object SignedMessageSignatureSerializer : KSerializer<SignedMessage.Signature> {
    private val byteArraySerializer = ByteArraySerializer()
    override val descriptor = SerialDescriptor("SignedMessageSignature", byteArraySerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: SignedMessage.Signature
    ) = encoder.encodeSerializableValue(byteArraySerializer, value.bytes())

    override fun deserialize(decoder: Decoder) =
        SignedMessage.signature(decoder.decodeSerializableValue(byteArraySerializer))
}