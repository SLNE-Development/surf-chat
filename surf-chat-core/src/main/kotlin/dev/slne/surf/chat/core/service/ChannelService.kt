package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface ChannelService {
    fun createChannel(name: String, owner: User): Channel
    fun deleteChannel(channel: Channel)
    fun getChannel(name: String): Channel?
    fun getChannel(channelUuid: UUID): Channel?
    fun getChannel(member: ChannelMember): Channel?
    fun getChannels(): ObjectSet<Channel>

    fun registerChannel(channel: Channel)
    fun unregisterChannel(channel: Channel)
    fun getRegisteredChannels(): ObjectSet<Channel>

    fun move(user: User, channel: Channel): Boolean
}