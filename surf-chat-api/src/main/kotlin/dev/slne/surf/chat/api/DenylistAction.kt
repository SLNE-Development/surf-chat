package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.entry.DenylistActionType
import net.kyori.adventure.text.Component

/**
 * Represents an action to be applied to a user in the denylist system.
 * This action is typically used for enforcement of chat rules
 * and allows specifying the type of action, its duration, and the reason for it.
 */
interface DenylistAction {
    /**
     * The unique name identifying the denylist action.
     */
    val name: String

    /**
     * Represents the type of action associated with a denylist entry.
     *
     * The `actionType` specifies what kind of moderation action is performed,
     * such as banning, kicking, muting, or warning a user. This helps categorize
     * and identify the specific action type for denylist entries.
     */
    val actionType: DenylistActionType

    /**
     * Represents the reason associated with a specific denylist action.
     * This reason is displayed as a component and provides contextual
     * information about the action being performed.
     */
    val reason: Component

    /**
     * The duration of the denylist action, represented in milliseconds.
     *
     * This value determines the length of time for which the denylist action remains in effect.
     */
    val duration: Long
}