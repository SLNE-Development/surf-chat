package dev.slne.surf.chat.api.message

import kotlinx.serialization.Serializable

@Serializable
/**
 * Represents the type of a message in the chat system.
 */
enum class MessageType {
    /** A global message visible to all users. */
    GLOBAL,

    /** A message sent to a team. */
    TEAM,

    /** A direct message sent to a specific user. */
    DIRECT
}