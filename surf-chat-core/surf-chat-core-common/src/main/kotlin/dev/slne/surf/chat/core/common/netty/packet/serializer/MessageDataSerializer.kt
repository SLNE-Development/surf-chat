package dev.slne.surf.chat.core.common.netty.packet.serializer

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.cloud.api.common.netty.network.codec.kotlinx.cloud.CloudPlayerSerializer
import dev.slne.surf.cloud.api.common.netty.network.codec.kotlinx.java.UUIDSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

@OptIn(ExperimentalSerializationApi::class)
class MessageDataSerializer : KSerializer<MessageData> {
    override val descriptor = PrimitiveSerialDescriptor("MessageData", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MessageData
    ) {
        encoder.encodeString(GsonComponentSerializer.gson().serialize(value.message))
        encoder.encodeSerializableValue(UUIDSerializer, value.messageUuid)
        encoder.encodeSerializableValue(CloudPlayerSerializer, value.sender)
        encoder.encodeNullableSerializableValue(CloudPlayerSerializer, value.receiver)
        encoder.encodeLong(value.sentAt)
        encoder.encodeString(value.server)
        encoder.encodeString(value.channel ?: "null")
        encoder.encodeNullableSerializableValue(SignedMessageSignatureSerializer, value.signature)
        encoder.encodeString(value.type.name)
    }

    override fun deserialize(decoder: Decoder): MessageData {
        val message = GsonComponentSerializer.gson().deserialize(decoder.decodeString())
        val messageUuid = decoder.decodeSerializableValue(UUIDSerializer)
        val sender = decoder.decodeSerializableValue(CloudPlayerSerializer)
        val receiver = decoder.decodeNullableSerializableValue(CloudPlayerSerializer)
        val sentAt = decoder.decodeLong()
        val server = decoder.decodeString()
        val channel = decoder.decodeString().let {
            if (it == "null") null else it
        }
        val signature = decoder.decodeNullableSerializableValue(SignedMessageSignatureSerializer)
        val type = MessageType.valueOf(decoder.decodeString())

        return MessageData(
            message = message,
            messageUuid = messageUuid,
            sender = sender,
            receiver = receiver,
            sentAt = sentAt,
            server = server,
            channel = channel,
            signature = signature,
            type = type
        )
    }
}