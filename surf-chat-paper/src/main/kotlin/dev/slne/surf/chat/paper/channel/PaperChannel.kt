package dev.slne.surf.chat.paper.channel

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.channel.ChannelVisibility
import dev.slne.surf.chat.core.common.netty.packet.serializer.ChatUuid
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

data class PaperChannel(
    override val channelUuid: ChatUuid,
    override val channelName: String,
    override val members: ObjectSet<ChannelMember>,
    override val bannedPlayers: ObjectSet<UUID>,
    override val invitedPlayers: ObjectSet<UUID>,
    override var visibility: ChannelVisibility,
    override val createdAt: Long
) : Channel(
    channelUuid,
    channelName,
    members,
    bannedPlayers,
    invitedPlayers,
    visibility,
    createdAt
) {
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
}