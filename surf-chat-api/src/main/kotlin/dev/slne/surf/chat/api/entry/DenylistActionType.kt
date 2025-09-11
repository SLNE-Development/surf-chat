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
    BAN,

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
    WARN
}