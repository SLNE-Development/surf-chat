package dev.slne.surf.chat.api.channel

/**
 * Represents the different roles a user can have within a chat channel.
 * This enum defines the hierarchy and permissions associated with each role.
 */
enum class ChannelRole {
    /**
     * The owner of the channel, with the highest level of permissions.
     */
    OWNER,

    /**
     * A moderator of the channel, with elevated permissions to manage members.
     */
    MODERATOR,

    /**
     * A regular member of the channel, with standard permissions.
     */
    MEMBER
}