package dev.slne.surf.chat.api.model

/**
 * Represents the type of a message in the chat system.
 */
enum class MessageType {
    /** A global message visible to all users. */
    GLOBAL,

    /** A message sent within a specific channel. */
    CHANNEL,

    /** A message sent to a team. */
    TEAM,

    /** A direct message sent to a specific user. */
    DIRECT
}