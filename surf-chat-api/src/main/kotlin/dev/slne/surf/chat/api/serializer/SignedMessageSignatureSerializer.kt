package dev.slne.surf.chat.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.chat.SignedMessage
import java.util.*


typealias SerializableSignature = @Serializable(with = SignedMessageSignatureSerializer::class) SignedMessage.Signature

object SignedMessageSignatureSerializer : KSerializer<SignedMessage.Signature> {
    override val descriptor =
        PrimitiveSerialDescriptor("SignedMessageSignature", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: SignedMessage.Signature
    ) = encoder.encodeString(Base64.getEncoder().encodeToString(value.bytes()))

    override fun deserialize(decoder: Decoder) =
        SignedMessage.signature(Base64.getDecoder().decode(decoder.decodeString()))

}