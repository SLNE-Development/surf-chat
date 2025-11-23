package dev.slne.surf.chat.paper.channel

import dev.slne.surf.chat.api.ChatUuid
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.api.channel.ChannelVisibility
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.paper.util.channelMember
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChannelService {
    val channels = mutableMapOf<UUID, Channel>()
    fun createChannel(
        name: String,
        owner: CloudPlayer
    ): Channel {
        val members = mutableObjectSetOf<ChannelMember>()

        members.add(
            ChannelMember(
                owner.uuid, owner.name, ChannelRole.OWNER
            )
        )

        val channel = PaperChannel(
            UUID.randomUUID(), name, members, mutableObjectSetOf(), mutableObjectSetOf(),
            ChannelVisibility.PRIVATE, System.currentTimeMillis()
        )

        this.registerChannel(channel)
        return channel
    }

    fun deleteChannel(channel: Channel) {
        unregisterChannel(channel)
    }

    fun getChannel(name: String) =
        channels.values.firstOrNull { it.channelName == name }

    fun getChannel(channelUuid: ChatUuid) =
        channels[channelUuid]

    fun getChannel(user: CloudPlayer) =
        channels.values.firstOrNull { it.isMember(user) }

    fun getChannels(): ObjectSet<Channel> =
        mutableObjectSetOf(*channels.values.toTypedArray())

    fun invite(
        channel: Channel,
        user: CloudPlayer
    ) = channel.invite(user)

    fun uninvite(
        channel: Channel,
        user: CloudPlayer
    ) = channel.revoke(user)

    fun isInvited(
        channel: Channel,
        user: CloudPlayer
    ) = channel.isInvited(user)

    fun acceptInvite(
        channel: Channel,
        user: CloudPlayer
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

    fun declineInvite(
        channel: Channel,
        user: CloudPlayer
    ): Boolean {
        if (!channel.isInvited(user)) {
            return false
        }

        channel.revoke(user)
        return true
    }

    fun registerChannel(channel: Channel) {
        channels[channel.channelUuid] = channel
    }

    fun unregisterChannel(channel: Channel) {
        channels.remove(channel.channelUuid)
    }

    fun getRegisteredChannels(): ObjectSet<Channel> {
        return mutableObjectSetOf(*channels.values.toTypedArray())
    }

    fun move(
        user: CloudPlayer,
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

val channelService get() = ChatContextHolderImpl.instance.context.getBean<ChannelService>()