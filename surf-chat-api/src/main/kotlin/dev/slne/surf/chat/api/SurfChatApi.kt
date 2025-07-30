package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

/**
 * API for managing chat functionality in the system.
 */
interface SurfChatApi {

    /**
     * Logs a message in the chat system.
     *
     * @param message The content of the message.
     * @param type The type of the message (e.g., global, channel, etc.).
     * @param sender The user who sent the message.
     * @param receiver The user who received the message, or `null` if not applicable.
     * @param sentAt The timestamp (in milliseconds since epoch) when the message was sent. Defaults to the current time.
     * @param server The server where the message was sent. Defaults to "unspecified".
     * @param channel The channel where the message was sent, or `null` if not applicable.
     * @param signedMessage The signed message object, or `null` if not applicable.
     */
    suspend fun logMessage(
        message: Component,
        type: MessageType,
        sender: User,
        receiver: User? = null,
        sentAt: Long = System.currentTimeMillis(),
        server: String = "unspecified",
        channel: Channel? = null,
        signedMessage: SignedMessage? = null
    )

    /**
     * Retrieves a user by their name.
     *
     * @param name The name of the user.
     * @return The user object, or `null` if not found.
     */
    fun getUser(name: String): User?

    /**
     * Retrieves a user by their UUID.
     *
     * @param uuid The UUID of the user.
     * @return The user object, or `null` if not found.
     */
    fun getUser(uuid: UUID): User?

    /**
     * Creates a new user in the system.
     *
     * @param name The name of the user.
     * @param uuid The UUID of the user.
     * @return The created user object.
     */
    fun createUser(name: String, uuid: UUID): User

    /**
     * Looks up chat history based on a filter.
     *
     * @param filter The filter criteria for querying the history.
     * @return A set of history entries matching the filter.
     */
    suspend fun lookupHistory(filter: HistoryFilter): ObjectSet<HistoryEntry>

    /**
     * Creates a new chat channel.
     *
     * @param name The name of the channel.
     * @param owner The owner of the channel.
     * @return The created channel object.
     */
    fun createChannel(name: String, owner: User): Channel

    /**
     * Deletes a chat channel.
     *
     * @param channel The channel to delete.
     */
    fun deleteChannel(channel: Channel)

    /**
     * Retrieves a channel by its name.
     *
     * @param name The name of the channel.
     * @return The channel object, or `null` if not found.
     */
    fun getChannel(name: String): Channel?

    /**
     * Retrieves all available channels.
     *
     * @return A set of all channels.
     */
    fun getChannels(): ObjectSet<Channel>

    /**
     * Invites a user to a channel.
     *
     * @param channel The channel to invite the user to.
     * @param user The user to invite.
     * @return `true` if the invitation was successful, otherwise `false`.
     */
    fun invite(channel: Channel, user: User): Boolean

    /**
     * Revokes an invitation for a user to a channel.
     *
     * @param channel The channel to revoke the invitation from.
     * @param user The user whose invitation is to be revoked.
     * @return `true` if the revocation was successful, otherwise `false`.
     */
    fun uninvite(channel: Channel, user: User): Boolean

    /**
     * Checks if a user is invited to a channel.
     *
     * @param channel The channel to check.
     * @param user The user to check.
     * @return `true` if the user is invited, otherwise `false`.
     */
    fun isInvited(channel: Channel, user: User): Boolean

    /**
     * Accepts an invitation to a channel.
     *
     * @param channel The channel to join.
     * @param user The user accepting the invitation.
     * @return `true` if the invitation was successfully accepted, otherwise `false`.
     */
    fun acceptInvite(channel: Channel, user: User): Boolean

    /**
     * Declines an invitation to a channel.
     *
     * @param channel The channel to decline the invitation for.
     * @param user The user declining the invitation.
     * @return `true` if the invitation was successfully declined, otherwise `false`.
     */
    fun declineInvite(channel: Channel, user: User): Boolean

    companion object {
        /**
         * The singleton instance of the `SurfChatApi`.
         */
        val INSTANCE = requiredService<SurfChatApi>()
    }
}

/**
 * Provides access to the singleton instance of the `SurfChatApi`.
 */
val surfChatApi get() = SurfChatApi.INSTANCE