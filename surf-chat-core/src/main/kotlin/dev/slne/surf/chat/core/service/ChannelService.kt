package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

/**
 * Service interface for managing chat channels in the system.
 */
interface ChannelService {
    /**
     * Creates a new chat channel.
     *
     * @param name The name of the channel to create.
     * @param owner The user who will be the owner of the channel.
     * @return The created `Channel` instance.
     */
    fun createChannel(name: String, owner: User): Channel

    /**
     * Deletes the specified chat channel from the system.
     *
     * @param channel The channel to be deleted.
     */
    fun deleteChannel(channel: Channel)

    /**
     * Retrieves a chat channel by its name.
     *
     * @param name The name of the channel to retrieve.
     * @return The `Channel` instance corresponding to the specified name, or `null` if no such channel exists.
     */
    fun getChannel(name: String): Channel?

    /**
     * Retrieves a chat channel by its unique identifier (UUID).
     *
     * @param channelUuid The unique identifier of the channel to retrieve.
     * @return The `Channel` instance corresponding to the specified UUID, or `null` if no such channel exists.
     */
    fun getChannel(channelUuid: UUID): Channel?

    /**
     * Retrieves the channel associated with a given user.
     *
     * @param user The user for whom the channel is being retrieved.
     * @return The `Channel` instance associated with the specified user, or `null` if no associated channel exists.
     */
    fun getChannel(user: User): Channel?

    /**
     *
     */
    fun getChannels(): ObjectSet<Channel>

    /**
     * Invites a user to a specified chat channel.
     *
     * @param channel The channel to which the user will be invited.
     * @param user The user being invited to the channel.
     * @return `true` if the invitation was successfully added, `false` otherwise.
     */
    fun invite(channel: Channel, user: User): Boolean

    /**
     * Revokes an invitation for a user to join a specified channel.
     *
     * @param channel The channel from which the invitation is to be revoked.
     * @param user The user whose invitation is being revoked.
     * @return `true` if the invitation was successfully revoked, otherwise `false`.
     */
    fun uninvite(channel: Channel, user: User): Boolean

    /**
     * Checks whether the given user has been invited to the specified channel.
     *
     * @param channel The channel to check for the invitation.
     * @param user The user to verify for the invitation status.
     * @return `true` if the user is invited to the channel, `false` otherwise.
     */
    fun isInvited(channel: Channel, user: User): Boolean

    /**
     * Accepts an invite for a user to join a specified channel.
     *
     * @param channel The channel the user is invited to.
     * @param user The user accepting the invite.
     * @return `true` if the invite was successfully accepted and the user joined the channel, otherwise `false`.
     */
    fun acceptInvite(channel: Channel, user: User): Boolean

    /**
     * Declines an invitation for a user to join a specified channel.
     *
     * @param channel The channel the invitation pertains to.
     * @param user The user declining the invitation.
     * @return `true` if the invitation was successfully declined, otherwise `false`.
     */
    fun declineInvite(channel: Channel, user: User): Boolean

    /**
     * Registers a channel in the system, making it available for use.
     *
     * @param channel The channel to be registered.
     */
    fun registerChannel(channel: Channel)

    /**
     * Unregisters the specified chat channel from the system, removing it from the list of active or registered channels.
     *
     * @param channel The channel to be unregistered.
     */
    fun unregisterChannel(channel: Channel)

    /**
     * Retrieves the set of all registered channels within the system.
     *
     * @return A set of registered channels, where each channel is represented by the Channel interface.
     */
    fun getRegisteredChannels(): ObjectSet<Channel>

    /**
     * Moves a user to a specified chat channel.
     *
     * @param user The user to be moved to the channel.
     * @param channel The target channel where the user will be moved.
     * @return `true` if the operation was successful, otherwise `false`.
     */
    fun move(user: User, channel: Channel): Boolean

    /**
     *
     */
    companion object {
        /**
         *
         */
        val INSTANCE = requiredService<ChannelService>()
    }
}

/**
 * A property to access the singleton instance of the ChannelService.
 * ChannelService provides functionality to manage and interact with chat channels in the system.
 */
val channelService get() = ChannelService.INSTANCE