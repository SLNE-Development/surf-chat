package dev.slne.surf.chat.api.channel

/**
 * Represents the status of a chat channel.
 * This enum defines whether a channel is publicly accessible or private.
 */
enum class ChannelStatus {
    /**
     * A public channel that is accessible to all users.
     */
    PUBLIC,

    /**
     * A private channel that is restricted to specific users.
     */
    PRIVATE
}