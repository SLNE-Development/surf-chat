package dev.slne.surf.chat.api.type

/**
 * Represents the status of a chat channel.
 * This enum defines whether a channel is publicly accessible or private.
 */
enum class ChannelStatusType {
    /**
     * A public channel that is accessible to all users.
     */
    PUBLIC,

    /**
     * A private channel that is restricted to specific users.
     */
    PRIVATE
}