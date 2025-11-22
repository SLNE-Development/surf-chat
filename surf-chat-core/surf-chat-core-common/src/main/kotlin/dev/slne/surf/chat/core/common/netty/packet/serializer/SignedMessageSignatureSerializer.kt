package dev.slne.surf.chat.core.common.netty.packet.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.chat.SignedMessage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
object SignedMessageSignatureSerializer : KSerializer<SignedMessage.Signature> {
    override val descriptor =
        PrimitiveSerialDescriptor("SignedMessageSignature", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: SignedMessage.Signature) {
        val base64 = Base64.encode(value.bytes())
        encoder.encodeString(base64)
    }

    override fun deserialize(decoder: Decoder): SignedMessage.Signature {
        val bytes = Base64.decode(decoder.decodeString())
        return SignedMessage.signature(bytes)
    }
}