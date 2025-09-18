package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*


/**
 * Service responsible for managing spying operations within the chat system.
 * Allows tracking and controlling spying activities on chat channels and private messages.
 */
interface SpyService {
    /**
     * Retrieves the list of spies for a given chat channel.
     *
     * @param channel The chat channel for which to fetch the spies.
     * @return A list of UUIDs representing the spies monitoring the specified channel.
     */
    fun getChannelSpies(channel: Channel): ObjectList<UUID>

    /**
     * Retrieves the list of spies monitoring a specific player's private messages.
     *
     * @param player The UUID of the player whose private message spies are to be fetched.
     * @return A list of UUIDs representing the spies currently monitoring the player's private messages.
     */
    fun getPrivateMessageSpies(player: UUID): ObjectList<UUID>

    /**
     * Adds a spy to monitor activities within a specified chat channel.
     *
     * @param player The UUID of the player to be registered as a channel spy.
     * @param channel The chat channel that the player will monitor.
     * @return `true` if the player was successfully added as a spy, otherwise `false`.
     */
    fun addChannelSpy(player: UUID, channel: Channel): Boolean

    /**
     * Removes a player's spy access to the specified chat channel.
     *
     * @param player The UUID of the player whose spying access is to be removed.
     * @param channel The chat channel from which the player is to be unregistered as a spy.
     * @return `true` if the player's spying access was successfully removed, otherwise `false`.
     */
    fun removeChannelSpy(player: UUID, channel: Channel): Boolean

    /**
     * Adds a spy for monitoring private messages between a specified player and target.
     *
     * @param player The UUID of the player who will act as the spy.
     * @param target The UUID of the target player whose private messages will be monitored by the spy.
     * @return `true` if the spy was successfully added, `false` otherwise.
     */
    fun addPrivateMessageSpy(player: UUID, target: UUID): Boolean

    /**
     * Removes spying on private messages between the specified player and target.
     *
     * @param player The UUID of the player who previously had spying access.
     * @param target The UUID of the target whose private messages were being spied on.
     * @return `true` if the spying access was successfully removed, otherwise `false`.
     */
    fun removePrivateMessageSpy(player: UUID, target: UUID): Boolean

    /**
     * Checks whether the specified chat channel has any active spies monitoring it.
     *
     * @param channel The chat channel to check for active spies.
     * @return `true` if the channel has active spies, otherwise `false`.
     */
    fun hasChannelSpies(channel: Channel): Boolean

    /**
     * Checks if the specified player has spies monitoring their private messages.
     *
     * @param player The UUID of the player to check for private message spies.
     * @return `true` if the player has private message spies, `false` otherwise.
     */
    fun hasPrivateMessageSpies(player: UUID): Boolean

    /**
     * Checks if the specified player is currently spying on any chat channels.
     *
     * @param player The UUID of the player being checked.
     * @return `true` if the player is actively spying on any chat channels, otherwise `false`.
     */
    fun isChannelSpying(player: UUID): Boolean

    /**
     * Checks if the specified player is currently set as spying on private messages.
     *
     * @param player The UUID of the player to check for private message spying status.
     * @return `true` if the player is spying on private messages, `false` otherwise.
     */
    fun isPrivateMessageSpying(player: UUID): Boolean

    /**
     * Clears all channel spying data associated with the specified player.
     * This operation removes the player from monitoring any chat channels they were spying on.
     *
     * @param player The UUID of the player whose channel spying data is to be cleared.
     */
    fun clearChannelSpies(player: UUID)

    /**
     * Clears all spies monitoring private messages for the specified player.
     *
     * This method removes all spying access associated with the player's private messages,
     * ensuring that no spies are currently monitoring their communications.
     *
     * @param player The UUID of the player whose private message spies are to be cleared.
     */
    fun clearPrivateMessageSpies(player: UUID)

    /**
     * Cleans up spy-related tracking or data associated with the specified player.
     *
     * The cleanup operation ensures that any references to the player's spying activities,
     * such as spying on channels or private messages, are removed to maintain data consistency.
     *
     * @param player The UUID of the player whose spy data is to be cleaned up.
     */
    fun cleanup(player: UUID)

    /**
     * Companion object for accessing the singleton instance of the SpyService.
     * SpyService is responsible for managing spying operations in the system,
     * including tracking players spying on channels or private messages.
     */
    companion object {
        /**
         * Singleton instance of the SpyService interface.
         *
         * SpyService is responsible for managing spying operations within the system.
         * This includes tracking users who are spying on channels or private messages,
         * adding or removing spies, and cleaning up spying data for specific users.
         */
        val INSTANCE = requiredService<SpyService>()
    }
}

/**
 *
 */
val spyService get() = SpyService.INSTANCE