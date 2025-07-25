package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface ChannelService {
    fun createChannel(name: String, owner: User): Channel
    fun deleteChannel(channel: Channel)
    fun getChannel(name: String): Channel?
    fun getChannel(channelUuid: UUID): Channel?
    fun getChannel(user: User): Channel?
    fun getChannels(): ObjectSet<Channel>

    fun invite(channel: Channel, user: User): Boolean
    fun uninvite(channel: Channel, user: User): Boolean
    fun isInvited(channel: Channel, user: User): Boolean

    fun acceptInvite(channel: Channel, user: User): Boolean
    fun declineInvite(channel: Channel, user: User): Boolean

    fun registerChannel(channel: Channel)
    fun unregisterChannel(channel: Channel)
    fun getRegisteredChannels(): ObjectSet<Channel>

    fun move(user: User, channel: Channel): Boolean

    companion object {
        val INSTANCE = requiredService<ChannelService>()
    }
}

val channelService get() = ChannelService.INSTANCE