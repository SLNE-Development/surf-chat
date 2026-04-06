package dev.slne.surf.chat.api.message

import dev.slne.surf.api.core.messages.adventure.plain
import dev.slne.surf.chat.api.serializer.SerializableSignature
import dev.slne.surf.core.api.common.SurfCoreApi
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import java.time.OffsetDateTime
import java.util.*

@Serializable
/**
 * Represents a message in the chat system, including its content, metadata, and context.
 */
data class MessageData(
    /**
     * Represents the textual content of a message.
     *
     * This property provides the main content of the message as a `Component`. It is used across various contexts including
     * message formatting, validation, and data handling. The content can be processed in multiple ways, such as editing,
     * displaying, or validating based on the message type and other metadata.
     */
    val message: @Contextual Component,

    /**
     * A unique identifier for a specific message.
     *
     * The messageUuid is used to identify individual messages
     * and is critical for operations such as deletion, logging,
     * and ensuring message integrity and traceability across the system.
     */
    val messageUuid: @Contextual UUID,

    /**
     * Represents the sender of the message within the chat system.
     *
     * This property refers to a `User` instance that encapsulates the details of the user
     * who sent the message, including their unique identifier, name, and other related properties.
     *
     * It is used throughout various messaging operations, such as formatting and validating
     * messages in different contexts, determining sender permissions, and providing additional
     * sender-specific interactions.
     */
    val sender: @Contextual UUID,

    /**
     * Represents the recipient of a message within the chat system.
     *
     * This property indicates the user intended to receive the message. It can be `null`
     * if the message is not directed to a specific user, such as global announcements
     *
     * Common use cases:
     * - Identifying the specific user who is the recipient of a private message.
     * - Checking permissions or contextual data related to the recipient during message processing.
     */
    val receiver: @Contextual UUID?,

    /**
     * Represents the timestamp when the message was sent, measured in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     */
    val sentAt: @Contextual OffsetDateTime,

    /**
     * Represents the chat server associated with the message.
     *
     * This server provides metadata and identification for where the message was sent
     * or received. It includes both a user-facing name for display purposes and an internal,
     * stable identifier for system-level referencing.
     *
     * The `server` property is particularly useful when messages or communication need to be
     * associated with specific servers in a multi-server environment.
     */
    val server: String,

    /**
     * Represents the signed metadata of the message.
     *
     * This property contains cryptographic information, such as a signature, to verify the authenticity and integrity
     * of the associated message. If `null`, the message is considered unsigned or its signature data is unavailable.
     */
    val signature: SerializableSignature?,
    /**
     * Defines the type of the message associated with this object.
     *
     * This variable specifies the category of the message within the chat system,
     * helping to determine its context, audience, and handling. Possible values
     * are defined in the `MessageType` enum, including:
     * - `GLOBAL`: Visible to all users.
     * - `TEAM`: Targeted to a team.
     * - `DIRECT`: Sent directly between users.
     */
    val type: MessageType
) {
    val plainMessage by lazy {
        message.plain()
    }

    suspend fun senderUser() = SurfCoreApi.getOfflinePlayer(sender) ?: error("Sender user $sender not found")
    suspend fun receiverUser() = receiver?.let { SurfCoreApi.getOfflinePlayer(it) }

    fun withReceiver(receiver: UUID?) = copy(receiver = receiver)
}