package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.ChannelRole
import dev.slne.surf.chat.api.model.ChannelVisibility
import dev.slne.surf.chat.bukkit.util.sendText
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
    override val visibility: ChannelVisibility,
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
        if (!this.isOwner(member)) {
            return
        }

        members.removeIf { it.role == ChannelRole.OWNER }
        members.removeIf { it.uuid == member.uuid }
        members.add(FallbackChannelMember(member.uuid, member.name, ChannelRole.OWNER))

        member.sendText {
            success("Du bist jetzt der Besitzer des Nachrichtenkanals ")
            variableValue(channelName)
        }
    }

    override fun leaveAndTransfer(member: ChannelMember) {
        if (this.isOwner(member)) {
            var nextOwner = this.members.firstOrNull { it.hasModeratorPermissions() }

            if (nextOwner == null) {
                nextOwner = this.members.firstOrNull { it.uuid != member.uuid }
            }

            if (nextOwner == null) {
                channelService.deleteChannel(this)

                member.sendText {
                    info("Der Kanal wurde aufgelöst.")
                }
                return
            }

            this.transfer(nextOwner)
        }

        this.removeMember(member)

        member.sendText {
            success("Du hast den Nachrichtenkanal ")
            variableValue(channelName)
            success(" verlassen.")
        }

        this.sendText {
            variableValue(member.name)
            info(" hat den Nachrichtenkanal verlasen.")
        }
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