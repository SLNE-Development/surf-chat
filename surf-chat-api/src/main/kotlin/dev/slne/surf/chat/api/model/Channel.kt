package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.entity.User
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

/**
 * Represents a chat channel in the system.
 *
 * @property channelUuid The unique identifier of the channel.
 * @property channelName The name of the channel.
 * @property members The set of members currently in the channel.
 * @property bannedPlayers The set of users banned from the channel.
 * @property invitedPlayers The set of users invited to the channel.
 * @property visibility The visibility status of the channel.
 * @property createdAt The timestamp (in milliseconds since epoch) when the channel was created.
 */
interface Channel {
    val channelUuid: UUID
    val channelName: String
    val members: ObjectSet<ChannelMember>
    val bannedPlayers: ObjectSet<User>
    val invitedPlayers: ObjectSet<User>
    var visibility: ChannelVisibility
    val createdAt: Long

    /**
     * Allows a user to join the channel.
     *
     * @param user The user attempting to join the channel.
     */
    fun join(user: User)

    /**
     * Removes a member from the channel.
     *
     * @param member The member to be removed.
     * @return `true` if the member was successfully removed, otherwise `false`.
     */
    fun removeMember(member: ChannelMember) = members.remove(member)

    /**
     * Checks if a user is a member of the channel.
     *
     * @param user The user to check.
     * @return `true` if the user is a member, otherwise `false`.
     */
    fun isMember(user: User): Boolean = members.any { it.uuid == user.uuid }

    /**
     * Checks if a user is the owner of the channel.
     *
     * @param user The user to check.
     * @return `true` if the user is the owner, otherwise `false`.
     */
    fun isOwner(user: User): Boolean =
        members.any { it.uuid == user.uuid && it.role == ChannelRole.OWNER }

    /**
     * Checks if a member is the owner of the channel.
     *
     * @param member The member to check.
     * @return `true` if the member is the owner, otherwise `false`.
     */
    fun isOwner(member: ChannelMember): Boolean =
        member.role == ChannelRole.OWNER

    /**
     * Transfers ownership of the channel to a specified member.
     *
     * @param member The member to transfer ownership to.
     */
    fun transfer(member: ChannelMember)

    /**
     * Leaves the channel and transfers ownership to another member.
     *
     * @param member The member to transfer ownership to.
     */
    fun leaveAndTransfer(member: ChannelMember)

    /**
     * Checks if a user is invited to the channel.
     *
     * @param user The user to check.
     * @return `true` if the user is invited, otherwise `false`.
     */
    fun isInvited(user: User) = invitedPlayers.contains(user)

    /**
     * Invites a user to the channel.
     *
     * @param user The user to invite.
     * @return `true` if the user was successfully invited, otherwise `false`.
     */
    fun invite(user: User) = invitedPlayers.add(user)

    /**
     * Revokes an invitation for a user.
     *
     * @param user The user whose invitation is to be revoked.
     * @return `true` if the invitation was successfully revoked, otherwise `false`.
     */
    fun revoke(user: User) = invitedPlayers.remove(user)

    /**
     * Promotes a member to a higher role in the channel.
     *
     * @param member The member to promote.
     * @return `true` if the promotion was successful, otherwise `false`.
     */
    fun promote(member: ChannelMember): Boolean

    /**
     * Demotes a member to a lower role in the channel.
     *
     * @param member The member to demote.
     * @return `true` if the demotion was successful, otherwise `false`.
     */
    fun demote(member: ChannelMember): Boolean

    /**
     * Bans a user from the channel.
     *
     * @param user The user to ban.
     */
    fun ban(user: User)

    /**
     * Unbans a user from the channel.
     *
     * @param user The user to unban.
     * @return `true` if the user was successfully unbanned, otherwise `false`.
     */
    fun unban(user: User) = bannedPlayers.remove(user)

    /**
     * Checks if a user is banned from the channel.
     *
     * @param user The user to check.
     * @return `true` if the user is banned, otherwise `false`.
     */
    fun isBanned(user: User): Boolean = bannedPlayers.contains(user)

    /**
     * Kicks a member from the channel.
     *
     * @param member The member to kick.
     */
    fun kick(member: ChannelMember)
}