package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

/**
 * Service responsible for managing spying operations within the chat system.
 * Allows tracking and controlling spying activities on private messages.
 */
interface SpyService {

    /**
     * Retrieves the list of spies monitoring a specific player's private messages.
     *
     * @param player The UUID of the player whose private message spies are to be fetched.
     * @return A list of UUIDs representing the spies currently monitoring the player's private messages.
     */
    fun getPrivateMessageSpies(player: UUID): Set<UUID>

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
     * Checks if the specified player is currently set as spying on private messages.
     *
     * @param player The UUID of the player to check for private message spying status.
     * @return `true` if the player is spying on private messages, `false` otherwise.
     */
    fun isPrivateMessageSpying(player: UUID): Boolean

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
     * such as spying on private messages, are removed to maintain data consistency.
     *
     * @param player The UUID of the player whose spy data is to be cleaned up.
     */
    fun cleanup(player: UUID)

    /**
     * Companion object for accessing the singleton instance of the SpyService.
     * SpyService is responsible for managing spying operations in the system,
     * including tracking players spying on private messages.
     */
    companion object {
        /**
         * Singleton instance of the SpyService interface.
         *
         * SpyService is responsible for managing spying operations within the system.
         * This includes tracking users who are spying on private messages,
         * adding or removing spies, and cleaning up spying data for specific users.
         */
        val INSTANCE = requiredService<SpyService>()
    }
}

/**
 *
 */
val spyService get() = SpyService.INSTANCE