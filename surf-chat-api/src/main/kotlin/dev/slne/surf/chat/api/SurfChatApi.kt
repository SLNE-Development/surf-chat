package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import org.springframework.beans.factory.getBean
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
     * @param channel The channel name where the message was sent, or `null` if not applicable.
     * @param signedMessage The signed message object, or `null` if not applicable.
     */
    suspend fun logMessage(
        message: Component,
        type: MessageType,
        sender: CloudPlayer,
        receiver: CloudPlayer? = null,
        sentAt: Long = System.currentTimeMillis(),
        server: CloudServer,
        channel: Channel? = null,
        signedMessage: SignedMessage? = null,
        messageUuid: ChatUuid = UUID.randomUUID()
    )

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
    fun createChannel(name: String, owner: CloudPlayer): Channel

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
    fun invite(channel: Channel, user: CloudPlayer): Boolean

    /**
     * Revokes an invitation for a user to a channel.
     *
     * @param channel The channel to revoke the invitation from.
     * @param user The user whose invitation is to be revoked.
     * @return `true` if the revocation was successful, otherwise `false`.
     */
    fun uninvite(channel: Channel, user: CloudPlayer): Boolean

    /**
     * Checks if a user is invited to a channel.
     *
     * @param channel The channel to check.
     * @param user The user to check.
     * @return `true` if the user is invited, otherwise `false`.
     */
    fun isInvited(channel: Channel, user: CloudPlayer): Boolean

    /**
     * Accepts an invitation to a channel.
     *
     * @param channel The channel to join.
     * @param user The user accepting the invitation.
     * @return `true` if the invitation was successfully accepted, otherwise `false`.
     */
    fun acceptInvite(channel: Channel, user: CloudPlayer): Boolean

    /**
     * Declines an invitation to a channel.
     *
     * @param channel The channel to decline the invitation for.
     * @param user The user declining the invitation.
     * @return `true` if the invitation was successfully declined, otherwise `false`.
     */
    fun declineInvite(channel: Channel, user: CloudPlayer): Boolean
}

@OptIn(InternalChatApi::class)
        /**
         * Provides access to the singleton instance of the `SurfChatApi`.
         */
val surfChatApi get() = ChatContextHolder.instance.context.getBean<SurfChatApi>()