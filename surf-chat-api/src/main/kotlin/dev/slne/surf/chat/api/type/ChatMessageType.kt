package dev.slne.surf.chat.api.type

/**
 * Represents the different types of chat messages.
 * This enum defines the categories of messages that can be sent or received
 * within the chat system.
 */
enum class ChatMessageType {
    /**
     * A global message visible to all users.
     */
    GLOBAL,

    /**
     * A message sent within a specific channel.
     */
    CHANNEL,

    /**
     * A private message sent from a user to another user.
     */
    PRIVATE_FROM,

    /**
     * A private message received by a user from another user.
     */
    PRIVATE_TO,

    /**
     * A private message exchanged between two users.
     */
    PRIVATE,

    /**
     * A message sent to a team or group of users.
     */
    TEAM,

    /**
     * An internal message used for system or administrative purposes.
     */
    INTERNAL,

    /**
     * A reply to a specific message.
     */
    REPLY
}