package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.api.channel.ChannelVisibility
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.ChannelService
import dev.slne.surf.chat.fallback.model.FallbackChannel
import dev.slne.surf.chat.fallback.user.FallbackChannelMember
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(ChannelService::class)
class FallbackChannelService : ChannelService, Services.Fallback {
    val channels = mutableMapOf<UUID, Channel>()
    override fun createChannel(
        name: String,
        owner: User
    ): Channel {
        val members = mutableObjectSetOf<ChannelMember>()

        members.add(
            FallbackChannelMember(
                owner.uuid, owner.name, ChannelRole.OWNER
            )
        )

        val channel = FallbackChannel(
            UUID.randomUUID(), name, members, mutableObjectSetOf(), mutableObjectSetOf(),
            ChannelVisibility.PRIVATE, System.currentTimeMillis()
        )

        this.registerChannel(channel)
        return channel
    }

    override fun deleteChannel(channel: Channel) {
        unregisterChannel(channel)
    }

    override fun getChannel(name: String) =
        channels.values.firstOrNull { it.channelName == name }

    override fun getChannel(channelUuid: UUID) =
        channels[channelUuid]

    override fun getChannel(user: User) =
        channels.values.firstOrNull { it.isMember(user) }

    override fun getChannels(): ObjectSet<Channel> =
        mutableObjectSetOf(*channels.values.toTypedArray())

    override fun invite(
        channel: Channel,
        user: User
    ) = channel.invite(user)

    override fun uninvite(
        channel: Channel,
        user: User
    ) = channel.revoke(user)

    override fun isInvited(
        channel: Channel,
        user: User
    ) = channel.isInvited(user)

    override fun acceptInvite(
        channel: Channel,
        user: User
    ): Boolean {
        if (!channel.isInvited(user)) {
            return false
        }

        this.getChannel(user)?.let {
            it.leaveAndTransfer(user.channelMember(it) ?: return@let)
        }

        channel.join(user)
        channel.revoke(user)
        return true
    }

    override fun declineInvite(
        channel: Channel,
        user: User
    ): Boolean {
        if (!channel.isInvited(user)) {
            return false
        }

        channel.revoke(user)
        return true
    }

    override fun registerChannel(channel: Channel) {
        channels[channel.channelUuid] = channel
    }

    override fun unregisterChannel(channel: Channel) {
        channels.remove(channel.channelUuid)
    }

    override fun getRegisteredChannels(): ObjectSet<Channel> {
        return mutableObjectSetOf(*channels.values.toTypedArray())
    }

    override fun move(
        user: User,
        channel: Channel
    ): Boolean {
        val currentChannel = this.getChannel(user)

        currentChannel?.let { currentChannel ->
            val currentMember = user.channelMember(currentChannel)

            currentMember?.let {
                currentChannel.removeMember(currentMember)
            }
        }

        channel.join(user)
        return true
    }
}