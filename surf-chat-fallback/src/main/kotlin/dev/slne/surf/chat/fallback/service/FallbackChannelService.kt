package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.core.service.ChannelService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(ChannelService::class)
class FallbackChannelService : ChannelService, Services.Fallback {
    override fun createChannel(
        name: String,
        owner: User
    ): Channel {
        TODO("Not yet implemented")
    }

    override fun deleteChannel(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun getChannel(name: String): Channel? {
        TODO("Not yet implemented")
    }

    override fun getChannel(channelUuid: UUID): Channel? {
        TODO("Not yet implemented")
    }

    override fun getChannel(user: User): Channel? {
        return null
    }

    override fun getChannels(): ObjectSet<Channel> {
        TODO("Not yet implemented")
    }

    override fun registerChannel(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun unregisterChannel(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun getRegisteredChannels(): ObjectSet<Channel> {
        TODO("Not yet implemented")
    }

    override fun move(
        user: User,
        channel: Channel
    ): Boolean {
        TODO("Not yet implemented")
    }

}