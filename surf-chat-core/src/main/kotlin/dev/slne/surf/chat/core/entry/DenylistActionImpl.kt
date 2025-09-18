package dev.slne.surf.chat.core.entry

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistActionType

/**
 * Implementation of the `DenylistAction` interface.
 *
 * This data class represents an action to be applied on a user in the denylist system,
 * allowing for enforcement of moderation rules. It includes details such as the name
 * of the action, its type, the reason for the action, and the duration for which the action
 * is effective.
 *
 * @property name The unique name identifying the denylist action.
 * @property actionType Specifies the type of action, such as banning, muting, kicking, or warning.
 * @property reason The reason associated with this action, represented as a string for contextual information.
 * @property duration The duration of the denylist action in milliseconds.
 */
data class DenylistActionImpl(
    override val name: String,
    override val actionType: DenylistActionType,
    override val reason: String,
    override val duration: Long
) : DenylistAction