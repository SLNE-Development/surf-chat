package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.api.channel.ChannelVisibility
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.fallback.entity.FallbackChannelMember
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

data class FallbackChannel(
    override val channelUuid: UUID,
    override val channelName: String,
    override val members: ObjectSet<ChannelMember>,
    override val bannedPlayers: ObjectSet<User>,
    override val invitedPlayers: ObjectSet<User>,
    override var visibility: ChannelVisibility,
    override val createdAt: Long
) : Channel {
    override fun join(user: User) {
        members.add(
            FallbackChannelMember(
                user.uuid, user.name, ChannelRole.MEMBER
            )
        )
    }

    override fun transfer(member: ChannelMember) {
        members.removeIf { it.role == ChannelRole.OWNER }
        members.removeIf { it.uuid == member.uuid }
        members.add(FallbackChannelMember(member.uuid, member.name, ChannelRole.OWNER))
    }

    override fun leaveAndTransfer(member: ChannelMember) {
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

    override fun promote(member: ChannelMember): Boolean {
        if (member.role == ChannelRole.MODERATOR) {
            return false
        }

        member.role = ChannelRole.MODERATOR

        members.remove(member)
        return members.add(member)
    }

    override fun demote(member: ChannelMember): Boolean {
        if (member.role == ChannelRole.MEMBER) {
            return false
        }

        member.role = ChannelRole.MEMBER

        members.remove(member)
        return members.add(member)
    }

    override fun ban(user: User) {
        if (this.isBanned(user)) {
            return
        }

        members.removeIf { it.uuid == user.uuid }
        bannedPlayers.add(user)
    }

    override fun kick(member: ChannelMember) {
        if (!members.contains(member)) {
            return
        }

        leaveAndTransfer(member)
    }
}