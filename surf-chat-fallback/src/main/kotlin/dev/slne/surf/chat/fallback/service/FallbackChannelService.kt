package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.core.service.ChannelService
import dev.slne.surf.chat.fallback.model.FallbackChannel
import dev.slne.surf.chat.fallback.model.FallbackChannelMember
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services

@AutoService(ChannelService::class)
class FallbackChannelService : ChannelService, Services.Fallback {
    val channels = mutableObjectSetOf<Channel>()

    override fun createChannel(
        name: String,
        owner: ChatUser,
        ownerName: String
    ): Channel {
        val channel = FallbackChannel(
            name = name
        )

        channel.members.add(FallbackChannelMember (
            uuid = owner.uuid,
            name = ownerName,
            role = ChannelRole.OWNER
        ))
        channels.add(channel)
        return channel
    }

    override fun deleteChannel(channel: Channel) {
        channel.members.forEach {
            channel.handleLeave(it)
        }
        channels.removeIf { it.name == channel.name }
    }

    override fun getChannel(name: String): Channel? {
        return channels.firstOrNull { it.name == name }
    }

    override fun getChannel(user: ChatUser): Channel? {
        return channels.firstOrNull { it.members.any { member -> member.uuid == user.uuid } }
    }

    override fun getAllChannels(): ObjectSet<Channel> {
        return channels
    }

    override fun register(channel: Channel) {
        if (channels.any { it.name == channel.name }) {
            return
        }
        channels.add(channel)
    }

    override fun unregister(channel: Channel) {
        channels.removeIf { it.name == channel.name }
    }

    override suspend fun move(
        user: ChatUser,
        channel: Channel
    ) {
        val currentChannel = this.getChannel(user)

        currentChannel?.leave(currentChannel.members.firstOrNull { it.uuid == user.uuid } ?: return, silent = false)

        channel.join(user, silent = false)
    }

    override fun handleDisconnect(user: ChatUser) {
        channels.forEach { channel ->
            channel.members.forEach {
                if (it.uuid == user.uuid) {
                    channel.handleLeave(it)
                }
            }
        }
    }
}