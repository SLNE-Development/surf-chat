package dev.slne.surf.chat.api.channel

import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.cloud.api.common.player.CloudPlayer
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
data class Channel(
    val channelUuid: UUID,
    val channelName: String,
    val members: ObjectSet<ChannelMember>,
    val bannedPlayers: ObjectSet<UUID>,
    val invitedPlayers: ObjectSet<UUID>,
    var visibility: ChannelVisibility,
    val createdAt: Long
) {
    /**
     * Allows a user to join the channel.
     *
     * @param user The user attempting to join the channel.
     */
    fun join(user: CloudPlayer) {
        members.add(ChannelMember(user.uuid, user.name, ChannelRole.MEMBER))
    }

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
    fun isMember(user: CloudPlayer): Boolean = members.any { it.uuid == user.uuid }

    /**
     * Checks if a user is the owner of the channel.
     *
     * @param user The user to check.
     * @return `true` if the user is the owner, otherwise `false`.
     */
    fun isOwner(user: CloudPlayer): Boolean =
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
    fun transfer(member: ChannelMember) {
        members.removeIf { it.role == ChannelRole.OWNER }
        members.removeIf { it.uuid == member.uuid }
        members.add(ChannelMember(member.uuid, member.name, ChannelRole.OWNER))
    }

    /**
     * Leaves the channel and transfers ownership to another member.
     *
     * @param member The member to transfer ownership to.
     */
    fun leaveAndTransfer(member: ChannelMember) {
        if (this.isOwner(member)) {
            var nextOwner =
                this.members.firstOrNull { it.hasModeratorPermissions() && it.uuid != member.uuid }

            if (nextOwner == null) {
                nextOwner = this.members.firstOrNull { it.uuid != member.uuid }
            }

            if (nextOwner == null) {
                channelService.deleteChannel(this)
                return
            }

            this.transfer(nextOwner)
        }

        this.removeMember(member)
    }

    /**
     * Checks if a user is invited to the channel.
     *
     * @param user The user to check.
     * @return `true` if the user is invited, otherwise `false`.
     */
    fun isInvited(user: CloudPlayer) = invitedPlayers.contains(user.uuid)

    /**
     * Invites a user to the channel.
     *
     * @param user The user to invite.
     * @return `true` if the user was successfully invited, otherwise `false`.
     */
    fun invite(user: CloudPlayer) = invitedPlayers.add(user.uuid)

    /**
     * Revokes an invitation for a user.
     *
     * @param user The user whose invitation is to be revoked.
     * @return `true` if the invitation was successfully revoked, otherwise `false`.
     */
    fun revoke(user: CloudPlayer) = invitedPlayers.remove(user.uuid)

    /**
     * Promotes a member to a higher role in the channel.
     *
     * @param member The member to promote.
     * @return `true` if the promotion was successful, otherwise `false`.
     */
    fun promote(member: ChannelMember): Boolean {
        if (member.role == ChannelRole.MODERATOR) {
            return false
        }

        member.role = ChannelRole.MODERATOR

        members.remove(member)
        return members.add(member)
    }

    /**
     * Demotes a member to a lower role in the channel.
     *
     * @param member The member to demote.
     * @return `true` if the demotion was successful, otherwise `false`.
     */
    fun demote(member: ChannelMember): Boolean {
        if (member.role == ChannelRole.MEMBER) {
            return false
        }

        member.role = ChannelRole.MEMBER

        members.remove(member)
        return members.add(member)
    }

    /**
     * Bans a user from the channel.
     *
     * @param user The user to ban.
     */
    fun ban(user: CloudPlayer) {
        if (this.isBanned(user)) {
            return
        }

        members.removeIf { it.uuid == user.uuid }
        bannedPlayers.add(user.uuid)
    }

    /**
     * Unbans a user from the channel.
     *
     * @param user The user to unban.
     * @return `true` if the user was successfully unbanned, otherwise `false`.
     */
    fun unban(user: CloudPlayer) = bannedPlayers.remove(user.uuid)

    /**
     * Checks if a user is banned from the channel.
     *
     * @param user The user to check.
     * @return `true` if the user is banned, otherwise `false`.
     */
    fun isBanned(user: CloudPlayer): Boolean = bannedPlayers.contains(user.uuid)

    /**
     * Kicks a member from the channel.
     *
     * @param member The member to kick.
     */
    fun kick(member: ChannelMember) {
        if (!members.contains(member)) {
            return
        }

        leaveAndTransfer(member)
    }
}