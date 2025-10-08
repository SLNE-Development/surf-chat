package dev.slne.surf.chat.api.entry

/**
 * Represents the type of action to be taken for a denylist entry.
 *
 * This enum defines the possible actions that can be applied when a denylist rule is triggered
 * in the chat system, such as banning, kicking, muting, or warning a user.
 */
enum class DenylistActionType {
    /**
     * Represents banning as an action type within the denylist system.
     *
     * This action type is used to prevent a user from accessing specified features or areas
     * based on the denylist conditions. It is part of the `DenylistActionType` enumeration
     * and is typically utilized in systems for moderating and managing user behavior.
     */
    EXPIREABLE_BAN,

    /**
     * Represents a permanent ban action type within the denylist system.
     *
     * This action type is used to permanently prevent a user from accessing certain features
     * or areas of the system based on the denylist conditions. Unlike temporary bans, this
     * action has no expiration and is intended for severe or repeated violations of the system's policies.
     */
    PERMANENT_BAN,

    /**
     * Represents the action of kicking a user from a chat or server.
     *
     * This action is typically used to temporarily remove a user without imposing
     * additional restrictions or long-term consequences.
     */
    KICK,

    /**
     * Represents the "MUTE" action type for a denylist entry.
     *
     * The "MUTE" action typically refers to temporarily restricting
     * a user's ability to send messages or interact in a communication environment.
     */
    MUTE,

    /**
     * Represents an action type for issuing a warning.
     *
     * This action type is typically used to notify users of improper behavior or activity
     * without enforcing stricter actions such as banning or muting. Warnings are generally
     * used as a preliminary measure in moderation workflows.
     */
    WARN,

    /**
     * Represents a community ban action type.
     *
     * This action type is used to enforce a ban across an entire community or platform,
     * rather than just a single server or chat instance. It is typically applied for severe
     * violations of community guidelines or rules.
     *
     * Note: Triggering a community ban can broadcast a message to discord
     */
    COMMUNITY_BAN
}