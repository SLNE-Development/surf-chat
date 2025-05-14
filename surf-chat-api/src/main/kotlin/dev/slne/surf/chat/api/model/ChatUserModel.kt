package dev.slne.surf.chat.api.model

import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

/**
 * Represents a chat user model.
 * This interface defines the structure and behavior of a chat user,
 * including their preferences, ignore lists, and channel interactions.
 */
interface ChatUserModel {

    /**
     * The unique identifier (UUID) of the user.
     */
    val uuid: UUID

    /**
     * A set of UUIDs representing users that this user is ignoring.
     */
    val ignoreList: ObjectSet<UUID>

    /**
     * Indicates whether private messages (PMs) are toggled on or off.
     */
    var pmToggled: Boolean

    /**
     * Indicates whether the user likes sound notifications.
     */
    var likesSound: Boolean

    /**
     * Indicates whether the user allows channel invites.
     */
    var channelInvites: Boolean

    /**
     * Ignores all channel invites for the user.
     */
    fun ignoreChannelInvites()

    /**
     * Stops ignoring channel invites for the user.
     */
    fun unignoreChannelInvites()

    /**
     * Toggles the user's preference for receiving channel invites.
     * @return The new state of the channel invite preference.
     */
    fun toggleChannelInvites(): Boolean

    /**
     * Checks if the user is currently ignoring channel invites.
     * @return True if the user is ignoring channel invites, false otherwise.
     */
    fun isIgnoringChannelInvites(): Boolean

    /**
     * Checks if the user is ignoring a specific target user.
     * @param target The UUID of the target user.
     * @return True if the target is being ignored, false otherwise.
     */
    fun isIgnoring(target: UUID): Boolean

    /**
     * Adds a target user to the ignore list.
     * @param target The UUID of the target user to ignore.
     */
    fun ignore(target: UUID)

    /**
     * Removes a target user from the ignore list.
     * @param target The UUID of the target user to unignore.
     */
    fun unIgnore(target: UUID)

    /**
     * Toggles the ignore state for a target user.
     * @param target The UUID of the target user.
     * @return The new ignore state for the target user.
     */
    fun toggleIgnore(target: UUID): Boolean

    /**
     * Toggles the user's preference for sound notifications.
     * @return The new state of the sound preference.
     */
    fun toggleSound(): Boolean

    /**
     * Accepts an invite to a specific channel.
     * @param channel The channel to accept the invite for.
     */
    fun acceptInvite(channel: ChannelModel)

    /**
     * Declines an invite to a specific channel.
     * @param channel The channel to decline the invite for.
     */
    fun declineInvite(channel: ChannelModel)

    /**
     * Checks if the user has private messages (PMs) open.
     * @return True if PMs are open, false otherwise.
     */
    fun hasOpenPms(): Boolean

    /**
     * Toggles the user's private message (PM) state.
     * @return The new state of the PM preference.
     */
    fun togglePm(): Boolean

    /**
     * Moves the user to a specific channel.
     * @param channel The channel to move the user to.
     */
    fun moveToChannel(channel: ChannelModel)

    /**
     * Retrieves the name of the user.
     * @return The name of the user as a String.
     */
    fun getName(): String
}