package dev.slne.surf.chat.api.user

import dev.slne.surf.chat.api.channel.Channel
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
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

    val name: String

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
    suspend fun togglePingsEnabled(): Boolean
    suspend fun togglePmsEnabled(): Boolean
    suspend fun togglePmFriendBypassEnabled(): Boolean

    suspend fun channelInvitesEnabled(): Boolean
    suspend fun pingsEnabled(): Boolean
    suspend fun pmEnabled(): Boolean
    suspend fun pmFriendBypassEnabled(): Boolean

    suspend fun ignores(target: UUID): Boolean
    suspend fun toggleIgnore(target: UUID): Boolean

    suspend fun acceptInvite(channel: Channel)
    suspend fun declineInvite(channel: Channel)
    suspend fun moveToChannel(channel: Channel)
}