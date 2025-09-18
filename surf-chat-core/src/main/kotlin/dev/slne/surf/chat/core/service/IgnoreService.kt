package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

/**
 * Service interface for managing player ignore actions in a chat system.
 *
 * The IgnoreService provides methods to ignore and un-ignore players,
 * check if a player is ignored by another, and retrieve a player's ignore list.
 * It is responsible for the underlying database operations necessary to persist
 * and retrieve ignore information.
 */
interface IgnoreService : DatabaseTableHolder {
    /**
     * Adds the specified target player to the ignore list of the given player.
     *
     * @param player The UUID of the player who is ignoring another player.
     * @param playerName The name of the player who is ignoring another player.
     * @param target The UUID of the target player to be ignored.
     * @param targetPlayerName The name of the target player to be ignored.
     */
    suspend fun ignore(player: UUID, playerName: String, target: UUID, targetPlayerName: String)

    /**
     * Removes an ignored status between the player and the target,
     * allowing them to interact or communicate again.
     *
     * @param player The UUID of the player who is removing the ignore status.
     * @param target The UUID of the player being unignored.
     */
    suspend fun unIgnore(player: UUID, target: UUID)

    /**
     * Checks if a given target player is ignored by the specified player.
     *
     * @param player The UUID of the player performing the check.
     * @param target The UUID of the potential ignored target.
     * @return `true` if the target player is ignored by the specified player, `false` otherwise.
     */
    suspend fun isIgnored(player: UUID, target: UUID): Boolean

    /**
     * Retrieves the ignore list of a specific player.
     *
     * The ignore list contains entries that represent players being ignored by the specified player.
     *
     * @param player the unique identifier of the player whose ignore list is being fetched
     * @return a set of {@link IgnoreListEntry} objects representing the players ignored by the specified player
     */
    suspend fun getIgnoreList(player: UUID): ObjectSet<IgnoreListEntry>

    /**
     * Companion object for accessing the singleton instance of the IgnoreService.
     * Provides centralized access to the functionalities of the IgnoreService.
     */
    companion object {
        /**
         * Singleton instance of the IgnoreService interface.
         *
         * IgnoreService is responsible for managing ignore operations in the system, including adding or removing
         * players from ignore lists and checking the ignored status between players.
         */
        val INSTANCE = requiredService<IgnoreService>()
    }
}

/**
 * A property to access the singleton instance of the IgnoreService.
 * IgnoreService provides functionality for managing ignored entities
 * or interactions within the system, typically used to handle ignored
 * players or messages in a chat or similar context.
 */
val ignoreService get() = IgnoreService.INSTANCE