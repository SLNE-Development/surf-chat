package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.api.channel.ChannelStatus
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet

class FallbackChannel(
    override val name: String,
    override var status: ChannelStatus = ChannelStatus.PRIVATE,
    override val members: ObjectSet<ChannelMember> = mutableObjectSetOf(),
    override val bannedPlayers: ObjectSet<ChatUser> = mutableObjectSetOf(),
    override val invites: ObjectSet<ChatUser> = mutableObjectSetOf()
) : Channel {
    override suspend fun join(user: ChatUser, silent: Boolean) {
        this.members.add(FallbackChannelMember(
            uuid = user.uuid,
            name = PlayerLookupService.getUsername(user.uuid) ?: "Unbekannt",
            role = ChannelRole.MEMBER

        ))
    }

    override fun leave(user: ChannelMember, silent: Boolean) {
        this.members.removeIf { it.uuid == user.uuid }
    }

    override fun isInvited(user: ChatUser): Boolean {
        return this.invites.any { it.uuid == user.uuid }
    }

    override fun invite(user: ChatUser) {
        if (this.isInvited(user)) {
            return
        }
        this.invites.add(user)
    }

    override fun revokeInvite(user: ChatUser) {
        this.invites.removeIf { it.uuid == user.uuid }
    }

    override fun transferOwnership(member: ChannelMember) {
        this.members.removeIf { it.role == ChannelRole.OWNER }
        this.members.add(FallbackChannelMember(
            uuid = member.uuid,
            name = member.name,
            role = ChannelRole.OWNER
        ))
        this.members.remove(member)
    }

    override fun promote(member: ChannelMember) {
        if (this.members.any { it.uuid == member.uuid && it.role == ChannelRole.MODERATOR }) {
            return
        }

        this.members.removeIf { it.uuid == member.uuid }
        this.members.add(FallbackChannelMember(
            uuid = member.uuid,
            name = member.name,
            role = ChannelRole.MODERATOR
        ))
    }

    override fun demote(member: ChannelMember) {
        if (this.members.any { it.uuid == member.uuid && it.role == ChannelRole.MEMBER }) {
            return
        }

        this.members.removeIf { it.uuid == member.uuid }
        this.members.add(FallbackChannelMember(
            uuid = member.uuid,
            name = member.name,
            role = ChannelRole.MEMBER
        ))
    }

    override fun kick(member: ChannelMember) {
        this.handleLeave(member)
    }

    override fun ban(user: ChatUser) {
        if (this.isBanned(user)) {
            return
        }

        this.bannedPlayers.add(user)
        this.invites.removeIf { it.uuid == user.uuid }

        val member = this.members.firstOrNull { it.uuid == user.uuid }

        if(member != null) {
            this.handleLeave(member)
        }
    }

    override fun unban(user: ChatUser) {
        if (!this.isBanned(user)) {
            return
        }

        this.bannedPlayers.removeIf { it.uuid == user.uuid }
    }

    override fun isBanned(user: ChatUser): Boolean {
        return this.bannedPlayers.any { it.uuid == user.uuid }
    }

    override fun getOwner(): ChannelMember {
        return this.members.firstOrNull { it.role == ChannelRole.OWNER }
            ?: throw IllegalStateException("No owner found in channel ${this.name}")
    }

    override fun isOwner(member: ChannelMember): Boolean {
        return this.members.any { it.uuid == member.uuid && it.role == ChannelRole.OWNER }
    }

    override fun hasModeratorPermissions(member: ChannelMember): Boolean {
        return this.members.any { it.uuid == member.uuid && (it.role == ChannelRole.OWNER || it.role == ChannelRole.MODERATOR) }
    }

    override fun isMember(user: ChatUser): Boolean {
        return this.members.any { it.uuid == user.uuid && it.role == ChannelRole.MEMBER }
    }

    override fun handleLeave(member: ChannelMember) {
        if (this.isOwner(member)) {
            var nextOwner = this.members.firstOrNull { it.role == ChannelRole.MODERATOR }

            if (nextOwner == null) {
                nextOwner = this.members.firstOrNull { it.role == ChannelRole.MEMBER }
            }

            if (nextOwner == null) {
                this.leave(member)
                channelService.deleteChannel(this)
                return
            }

            this.transferOwnership(nextOwner)
        }

        this.leave(member)
    }
}