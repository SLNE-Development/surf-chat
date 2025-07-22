package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.ChannelRole
import dev.slne.surf.chat.api.model.ChannelVisibility
import dev.slne.surf.chat.fallback.entity.FallbackChannelMember
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

data class FallbackChannel(
    override val channelUuid: UUID,
    override val channelName: String,
    override val members: ObjectSet<ChannelMember>,
    override val bannedPlayers: ObjectSet<User>,
    override val invitedPlayers: ObjectSet<User>,
    override val visibility: ChannelVisibility,
    override val createdAt: Long
) : Channel {
    override fun addMember(user: User) {
        members.add(
            FallbackChannelMember(
                user.uuid, ChannelRole.MEMBER
            )
        )
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

    override fun kick(member: ChannelMember): Boolean {
        if (!members.contains(member)) {
            return false
        }

        return members.remove(member)
    }
}