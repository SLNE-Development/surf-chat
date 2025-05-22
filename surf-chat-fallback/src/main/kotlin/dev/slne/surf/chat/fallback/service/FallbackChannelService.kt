package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.model.ChatUser
import dev.slne.surf.chat.core.service.ChannelService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services

@AutoService(ChannelService::class)
class FallbackChannelService : ChannelService, Services.Fallback {
    override fun createChannel(
        name: String,
        owner: ChatUser
    ): Channel {
        TODO("Not yet implemented")
    }

    override fun deleteChannel(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun getChannel(name: String): Channel? {
        TODO("Not yet implemented")
    }

    override fun getChannel(user: ChatUser): Channel? {
        TODO("Not yet implemented")
    }

    override fun getAllChannels(): ObjectSet<Channel> {
        TODO("Not yet implemented")
    }

    override fun register(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun unregister(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun move(
        user: ChatUser,
        channel: Channel
    ) {
        TODO("Not yet implemented")
    }

    override fun handleDisconnect(user: ChatUser) {
        TODO("Not yet implemented")
    }
}