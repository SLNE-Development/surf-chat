package dev.slne.surf.chat.core.client.redis.rpc

import dev.slne.surf.api.core.serializer.SurfSerializerModule
import dev.slne.surf.api.core.serializer.java.uuid.JavaUUIDStringSerializer
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.overwriteWith
import net.kyori.adventure.text.Component
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.security.KeyPairGenerator
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class SendDirectMessageRedisRequestTest {

    @Test
    fun `a signed message survives a round trip`() {
        val key = KeyPairGenerator.getInstance("RSA")
            .apply { initialize(2048) }
            .generateKeyPair()
            .public

        assertRoundTrips(
            signedChatMessage(
                signature = ByteArray(8) { it.toByte() },
                sessionId = SESSION_ID,
                lastSeen = listOf(ByteArray(3) { it.toByte() }, ByteArray(2) { it.toByte() }),
                sessionKey = SignedChatMessage.SessionKey(
                    EXPIRES_AT,
                    key,
                    ByteArray(4) { (it * 3).toByte() }
                )
            )
        )
    }

    @Test
    fun `a message from a sender without a chat session survives a round trip`() {
        assertRoundTrips(
            signedChatMessage(
                signature = null,
                sessionId = UUID(0L, 0L),
                lastSeen = emptyList(),
                sessionKey = null
            )
        )
    }

    private fun assertRoundTrips(message: SignedChatMessage) {
        val serializer = SendDirectMessageRedisRequest.serializer()
        val request = SendDirectMessageRedisRequest(messageData(), message)

        val encoded = json.encodeToString(serializer, request)
        val decoded = json.decodeFromString(serializer, encoded)

        assertEquals(encoded, json.encodeToString(serializer, decoded))
    }

    private fun signedChatMessage(
        signature: ByteArray?,
        sessionId: UUID,
        lastSeen: List<ByteArray>,
        sessionKey: SignedChatMessage.SessionKey?
    ) = SignedChatMessage(
        sender = SENDER,
        sessionId = sessionId,
        index = 7,
        signature = signature,
        content = "hello",
        timestamp = TIMESTAMP,
        salt = 4711L,
        lastSeen = lastSeen,
        unsignedContent = CONTENT,
        sessionKey = sessionKey
    )

    private fun messageData() = MessageData(
        CONTENT,
        UUID.fromString("6bb4f9b4-0c02-4a6a-9d0e-7f7f9a1b2c3d"),
        SENDER,
        RECEIVER,
        OffsetDateTime.of(2026, 8, 18, 12, 30, 0, 0, ZoneOffset.UTC),
        "lobby-1",
        null,
        MessageType.DIRECT
    )

    companion object {
        private val SENDER = UUID.fromString("11111111-2222-3333-4444-555555555555")
        private val RECEIVER = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa")
        private val SESSION_ID = UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff")

        private val CONTENT: Component = Component.text("hello")
        private val TIMESTAMP: Instant = Instant.ofEpochMilli(1_755_000_000_123L)
        private val EXPIRES_AT: Instant = Instant.ofEpochMilli(1_760_000_000_456L)

        /** Configured like the codec surf-redis encodes requests with. */
        @OptIn(ExperimentalSerializationApi::class)
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            namingStrategy = JsonNamingStrategy.SnakeCase
            serializersModule = SurfSerializerModule.all.overwriteWith(
                SerializersModule { contextual(JavaUUIDStringSerializer) }
            )
        }
    }
}
