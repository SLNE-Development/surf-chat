package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface ChannelService {
    /**
     * Creates and registers a new channel.
     *
     * @param name The name of the channel.
     * @param owner The owner of the channel.
     * @return The created ChannelModel.
     */
    fun createChannel(name: String, owner: ChatUserModel): ChannelModel

    /**
     * Deletes and unregisters an existing channel.
     *
     * @param channel The ChannelModel to delete.
     */
    fun deleteChannel(channel: ChannelModel)

    /**
     * Returns a channel by its name.
     *
     * @param name The name of the channel.
     * @return The ChannelModel or null if the channel does not exist.
     */
    fun getChannel(name: String): ChannelModel?

    /**
     * Returns a channel by a CommandSender.
     *
     * @param player The CommandSender (e.g., a player).
     * @return The ChannelModel or null if the channel does not exist.
     */
    fun getChannel(player: CommandSender): ChannelModel?

    /**
     * Returns all channels.
     *
     * @return A set of all ChannelModel objects.
     */
    fun getAllChannels(): ObjectSet<ChannelModel>

    /**
     * Registers a channel.
     *
     * @param channel The ChannelModel to register.
     */
    fun register(channel: ChannelModel)

    /**
     * Unregisters a channel.
     *
     * @param channel The ChannelModel to unregister.
     */
    fun unregister(channel: ChannelModel)

    /**
     * Moves a player to a specified channel.
     *
     * @param player The player to move.
     * @param channel The channel to move the player to.
     */
    fun move(player: Player, channel: ChannelModel)

    companion object {
        val INSTANCE = requiredService<ChannelService>()
    }
}

val channelService get() = ChannelService.INSTANCE