package dev.slne.surf.chat.api.channel

import dev.slne.surf.chat.api.user.ChatUser
import it.unimi.dsi.fastutil.objects.ObjectSet

/**
 * Represents a chat channel model.
 * This interface defines the structure and behavior of a chat channel,
 * including its members, status, and various operations for managing the channel.
 */
interface Channel {

    /**
     * The name of the channel.
     */
    val name: String

    /**
     * The current status of the channel (e.g., PUBLIC, PRIVATE).
     */
    var status: ChannelStatus

    /**
     * A set of channel members within the channel.
     */
    val members: ObjectSet<ChannelMember>

    /**
     * A set of players who are banned from the channel.
     */
    val bannedPlayers: ObjectSet<ChatUser>

    /**
     * A set of players who have been invited to the channel.
     */
    val invites: ObjectSet<ChatUser>

    /**
     * Allows a user to join the channel.
     * @param user The user joining the channel.
     * @param silent Whether the join action should be silent.
     */
    fun join(user: ChatUser, silent: Boolean = false)

    /**
     * Allows a user to leave the channel.
     * @param user The user leaving the channel.
     * @param silent Whether the leave action should be silent.
     */
    fun leave(user: ChatUser, silent: Boolean = false)

    /**
     * Checks if a user is invited to the channel.
     * @param user The user to check.
     * @return True if the user is invited, false otherwise.
     */
    fun isInvited(user: ChatUser): Boolean

    /**
     * Invites a user to the channel.
     * @param user The user to invite.
     */
    fun invite(user: ChatUser)

    /**
     * Revokes an invitation for a user.
     * @param user The user whose invitation is revoked.
     */
    fun revokeInvite(user: ChatUser)

    /**
     * Transfers ownership of the channel to another user.
     * @param member The new owner of the channel.
     */
    fun transferOwnership(member: ChannelMember)

    /**
     * Promotes a user to a higher role in the channel.
     * @param member The member to promote.
     */
    fun promote(member: ChannelMember)

    /**
     * Demotes a user to a lower role in the channel.
     * @param member The member to demote.
     */
    fun demote(member: ChannelMember)

    /**
     * Kicks a user from the channel.
     * @param member The member to kick.
     */
    fun kick(member: ChannelMember)

    /**
     * Bans a user from the channel.
     * @param user The user to ban.
     */
    fun ban(user: ChatUser)

    /**
     * Unbans a user from the channel.
     * @param user The user to unban.
     */
    fun unban(user: ChatUser)

    /**
     * Checks if a user is banned from the channel.
     * @param user The user to check.
     * @return True if the user is banned, false otherwise.
     */
    fun isBanned(user: ChatUser): Boolean

    /**
     * Gets the owner of the channel.
     * @return The owner of the channel.
     */
    fun getOwner(): ChannelMember

    /**
     * Checks if a user is the owner of the channel.
     * @param member The member to check.
     * @return True if the user is the owner, false otherwise.
     */
    fun isOwner(member: ChannelMember): Boolean

    /**
     * Checks if a user is a moderator of the channel.
     * @param member The member to check.
     * @return True if the user is a moderator, false otherwise.
     */
    fun hasModeratorPermissions(member: ChannelMember): Boolean

    /**
     * Checks if a user is a member of the channel.
     * @param user The user to check.
     * @return True if the user is a member, false otherwise.
     */
    fun isMember(user: ChatUser): Boolean
}