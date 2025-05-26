package dev.slne.surf.chat.api.user

import dev.slne.surf.chat.api.channel.Channel
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

/**
 * Represents a chat user model.
 * This interface defines the structure and behavior of a chat user,
 * including their preferences, ignore lists, and channel interactions.
 */
interface ChatUser {

    /**
     * The unique identifier (UUID) of the user.
     */
    val uuid: UUID

    /**
     * A set of UUIDs representing users that this user is ignoring.
     */
    val ignoreList: ObjectSet<UUID>

    /**
     * A data class containing the user's chat settings.
     */
    val settings: ChatUserSettings

    /**
     * Toggles the user's preference for receiving channel invites.
     * @return The new state of the channel invite preference.
     */
    suspend fun toggleChannelInvites(): Boolean

    /**
     * Checks if the user is ignoring a specific target user.
     * @param target The UUID of the target user.
     * @return True if the target is being ignored, false otherwise.
     */
    suspend fun isIgnoring(target: UUID): Boolean

    /**
     * Adds a target user to the ignore list.
     * @param target The UUID of the target user to ignore.
     */
    suspend fun ignore(target: UUID)

    /**
     * Removes a target user from the ignore list.
     * @param target The UUID of the target user to unignore.
     */
    suspend fun stopIgnoring(target: UUID)

    /**
     * Toggles the ignored state for a target user.
     * @param target The UUID of the target user.
     * @return The new ignore state for the target user.
     */
    suspend fun toggleIgnore(target: UUID): Boolean

    /**
     * Toggles the user's preference for sound notifications.
     * @return The new state of the sound preference.
     */
    suspend fun toggleSound(): Boolean

    /**
     * Accepts an invitation to a specific channel.
     * @param channel The channel to accept the invite for.
     */
    suspend fun acceptInvite(channel: Channel)

    /**
     * Declines an invitation to a specific channel.
     * @param channel The channel to decline the invite for.
     */
    suspend fun declineInvite(channel: Channel)

    /**
     * Checks if the user has private messages (PMs) open.
     * @return True if PMs are open, false otherwise.
     */
    suspend fun hasOpenPms(): Boolean

    /**
     * Toggles the user's private message (PM) state.
     * @return The new state of the PM preference.
     */
    suspend fun togglePm(): Boolean

    /**
     * Moves the user to a specific channel.
     * @param channel The channel to move the user to.
     */
    suspend fun moveToChannel(channel: Channel)

    /**
     * Retrieves the name of the user.
     * @return The name of the user as a String.
     */
    suspend fun getName(): String
}