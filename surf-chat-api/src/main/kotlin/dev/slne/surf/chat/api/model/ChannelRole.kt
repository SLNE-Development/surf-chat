package dev.slne.surf.chat.api.model

/**
 * Represents the role of a member in a chat channel.
 */
enum class ChannelRole {
    /** The owner of the channel, with the highest level of permissions. */
    OWNER,

    /** A moderator in the channel, with elevated permissions. */
    MODERATOR,

    /** A regular member of the channel. */
    MEMBER
}