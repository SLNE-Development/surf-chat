package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet

interface ChannelService {
    /**
     * Creates and registers a new channel.
     *
     * @param name The name of the channel.
     * @param owner The owner of the channel.
     * @return The created ChannelModel.
     */
    fun createChannel(name: String, owner: ChatUser, ownerName: String): Channel

    /**
     * Deletes and unregisters an existing channel.
     *
     * @param channel The ChannelModel to delete.
     */
    fun deleteChannel(channel: Channel)

    /**
     * Returns a channel by its name.
     *
     * @param name The name of the channel.
     * @return The ChannelModel or null if the channel does not exist.
     */
    fun getChannel(name: String): Channel?

    /**
     * Returns a channel by a user.
     *
     * @param user The user.
     * @return The ChannelModel or null if the channel does not exist.
     */
    fun getChannel(user: ChatUser): Channel?

    /**
     * Returns all channels.
     *
     * @return A set of all ChannelModel objects.
     */
    fun getAllChannels(): ObjectSet<Channel>

    /**
     * Registers a channel.
     *
     * @param channel The ChannelModel to register.
     */
    fun register(channel: Channel)

    /**
     * Unregisters a channel.
     *
     * @param channel The ChannelModel to unregister.
     */
    fun unregister(channel: Channel)

    /**
     * Moves a player to a specified channel.
     *
     * @param user The user to move.
     * @param channel The channel to move the player to.
     */
    suspend fun move(user: ChatUser, channel: Channel)

    /**
     * Handles the disconnection of a player from a specific channel.
     *
     * This method is called when a player disconnects from the server or leaves a channel.
     * It ensures that any necessary cleanup or state updates related to the player's
     * association with the channel are performed.
     *
     * @param user The player who is disconnecting.
     */
    fun handleDisconnect(user: ChannelMember)

    companion object {
        val INSTANCE = requiredService<ChannelService>()
    }
}

val channelService get() = ChannelService.INSTANCE