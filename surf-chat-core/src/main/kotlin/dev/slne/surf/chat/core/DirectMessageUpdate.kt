package dev.slne.surf.chat.core

import net.kyori.adventure.text.Component
import java.util.*

/**
 * Represents an update related to a direct message in the chat system.
 *
 * This data class is used to encapsulate all relevant details about a direct message event,
 * including its type, sender information, recipient information, and message content.
 *
 * @property type The type of update, indicating the action performed on the direct message (e.g., send, receive, log).
 * @property senderUuid The UUID of the sender of the message.
 * @property senderName The name of the sender of the message.
 * @property targetUuid The UUID of the recipient or target of the message.
 * @property targetName The name of the recipient or target of the message.
 * @property messageUuid A unique identifier associated with the specific message.
 * @property message The content of the message, represented as a Component.
 * @property sentAt The timestamp indicating when the message was sent, represented as milliseconds since the epoch.
 * @property serverName The name of the server involved in the direct message event.
 */
data class DirectMessageUpdate(
    val type: DirectMessageUpdateType,
    val senderUuid: UUID,
    val senderName: String,
    val targetUuid: UUID,
    val targetName: String,
    val messageUuid: UUID,
    val message: Component,
    val sentAt: Long,
    val serverName: String
)

/**
 * Represents the various types of updates that can be made to direct messages.
 *
 * This enum defines the specific operations or actions that can occur with direct messages within
 * the chat system, such as sending, receiving, logging, or a combination of these actions.
 */
enum class DirectMessageUpdateType {
    /**
     * Represents the action for sending a direct message within the chat system.
     *
     * This type is used as an indicator for handling direct message events
     * specifically related to sending messages from one user to another.
     * It is utilized in processing incoming plugin messages on the direct message
     * synchronization channel, where actions corresponding to this type are performed.
     *
     * The associated behavior is commonly implemented to:
     * - Transmit a direct message to the intended recipient.
     * - Ensure that the sender and message details are processed and forwarded to the recipient.
     */
    SEND_MESSAGE,

    /**
     * Enum constant representing an event where a direct message is received by a target user.
     *
     * This constant is used to indicate that the specified message should be processed
     * as a received direct message within the system. It is typically handled to deliver the
     * message contents to the target user and trigger any necessary notifications or updates
     * in the user interface or other components.
     *
     * Associated functionality:
     * - Triggers the internal message handling routine for received messages.
     * - Ensures the message is appropriately processed within the application context.
     *
     * Usage context:
     * - Utilized within message handling logic, specifically in scenarios where
     *   incoming messages are being categorized and processed for the end user.
     */
    RECEIVE_MESSAGE,

    /**
     * Represents an event to log a direct message within the chat system.
     *
     * This update type is used for handling operations where the details of a direct message
     * need to be recorded or persisted. It is typically utilized in scenarios where maintaining
     * a log of sent or received messages is required for auditing or debugging purposes.
     *
     * Associated functionality:
     * - Processes the event to ensure the message details are logged or saved.
     * - Integrates with systems that store or analyze message data.
     *
     * Usage context:
     * - Used as an indicator in message handling to denote that the message should be logged.
     * - Commonly paired with other update types, such as `SEND_MESSAGE`, for dual functionality.
     */
    LOG_MESSAGE,

    /**
     * Enum constant representing a combined operation of sending and logging a direct message within the chat system.
     *
     * This type signifies that the system should handle both sending the message to its intended recipient
     * and concurrently recording the message details in the log for tracking or archival purposes.
     *
     * Associated behavior:
     * - Sends the direct message to the specified recipient by processing the related data.
     * - Logs the message information into the system's storage or monitoring solution for reference.
     *
     * Usage context:
     * - Typically used in scenarios where both immediate message delivery and message state tracking are required.
     * - Can be triggered within the plugin's messaging system to perform dual operations sequentially.
     */
    SEND_AND_LOG_MESSAGE,
}