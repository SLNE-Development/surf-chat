package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.entity.User
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface Channel {
    val channelUuid: UUID
    val channelName: String
    val members: ObjectSet<ChannelMember>
    val bannedPlayers: ObjectSet<User>
    val invitedPlayers: ObjectSet<User>
    val visibility: ChannelVisibility
    val createdAt: Long

    fun addMember(user: User)
    fun removeMember(member: ChannelMember) = members.remove(member)
    fun isMember(user: User): Boolean = members.any { it.uuid == user.uuid }
    fun isOwner(user: User): Boolean =
        members.any { it.uuid == user.uuid && it.role == ChannelRole.OWNER }

    fun isOwner(member: ChannelMember): Boolean =
        member.role == ChannelRole.OWNER

    fun transfer(member: ChannelMember)
    fun leaveAndTransfer(member: ChannelMember)

    fun isInvited(user: User) = invitedPlayers.contains(user)
    fun invite(user: User) = invitedPlayers.add(user)
    fun revoke(user: User) = invitedPlayers.remove(user)

    fun promote(member: ChannelMember): Boolean
    fun demote(member: ChannelMember): Boolean

    fun ban(user: User) = bannedPlayers.add(user)
    fun unban(user: User) = bannedPlayers.remove(user)
    fun isBanned(user: User): Boolean = bannedPlayers.contains(user)
    fun kick(member: ChannelMember): Boolean
}