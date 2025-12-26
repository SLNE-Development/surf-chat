package dev.slne.surf.chat.api.denylist

/**
 * Represents an action to be applied to a user in the denylist system.
 * This action is typically used for enforcement of chat rules
 * and allows specifying the type of action, its duration, and the reason for it.
 */
data class DenylistAction(
    val name: String,
    val actionType: DenylistActionType,
    val reason: String,
    val duration: Long
)