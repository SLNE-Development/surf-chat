package dev.slne.surf.chat.api.channel

/**
 * Represents the visibility status of a chat channel.
 *
 * @property displayName The display name of the visibility status.
 */
enum class ChannelVisibility(val displayName: String) {
    /** The channel is visible to everyone. */
    PUBLIC("Öffentlich"),

    /** The channel is only visible to invited members. */
    PRIVATE("Privat")
}