package dev.slne.surf.chat.core.common.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.util.UUID

/**
 * Custom serializer for java.util.UUID.
 * Serializes UUID as a string in its standard format.
 */
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        val uuidString = decoder.decodeString()
        return try {
            UUID.fromString(uuidString)
        } catch (e: IllegalArgumentException) {
            throw kotlinx.serialization.SerializationException(
                "Invalid UUID format: '$uuidString'. Expected format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
                e
            )
        }
    }
}

/**
 * Object that provides UUID serialization support.
 * This object initializes the necessary serialization modules for UUID support.
 */
object SyncValues {
    /**
     * Serializers module that provides UUID serialization support.
     * This can be used to extend other serializers modules or create custom Json instances.
     */
    val serializersModule = SerializersModule {
        contextual(UUIDSerializer)
    }
    
    /**
     * Default Json configuration with UUID serialization support.
     * Use this Json instance for serialization/deserialization that involves UUIDs.
     */
    val json = Json {
        serializersModule = this@SyncValues.serializersModule
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
        isLenient = false
        coerceInputValues = false
    }
}
