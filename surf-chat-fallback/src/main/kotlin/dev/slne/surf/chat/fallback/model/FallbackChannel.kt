package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.channel.ChannelStatus
import dev.slne.surf.chat.api.user.ChatUser
import it.unimi.dsi.fastutil.objects.ObjectSet

class FallbackChannel(
    override val name: String,
    override var status: ChannelStatus,
    override val members: ObjectSet<ChannelMember>,
    override val bannedPlayers: ObjectSet<ChatUser>,
    override val invites: ObjectSet<ChatUser>
) : Channel {
    override fun join(user: ChatUser, silent: Boolean) {
        TODO("Not yet implemented")
    }

    override fun leave(user: ChatUser, silent: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isInvited(user: ChatUser): Boolean {
        TODO("Not yet implemented")
    }

    override fun invite(user: ChatUser) {
        TODO("Not yet implemented")
    }

    override fun revokeInvite(user: ChatUser) {
        TODO("Not yet implemented")
    }

    override fun transferOwnership(member: ChannelMember) {
        TODO("Not yet implemented")
    }

    override fun promote(member: ChannelMember) {
        TODO("Not yet implemented")
    }

    override fun demote(member: ChannelMember) {
        TODO("Not yet implemented")
    }

    override fun kick(member: ChannelMember) {
        TODO("Not yet implemented")
    }

    override fun ban(user: ChatUser) {
        TODO("Not yet implemented")
    }

    override fun unban(user: ChatUser) {
        TODO("Not yet implemented")
    }

    override fun isBanned(user: ChatUser): Boolean {
        TODO("Not yet implemented")
    }

    override fun getOwner(): ChannelMember {
        TODO("Not yet implemented")
    }

    override fun isOwner(member: ChannelMember): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasModeratorPermissions(member: ChannelMember): Boolean {
        TODO("Not yet implemented")
    }

    override fun isMember(user: ChatUser): Boolean {
        TODO("Not yet implemented")
    }
}