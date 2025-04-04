package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet

interface ChannelService {
    fun createChannel(name: String, owner: ChatUserModel): ChannelModel
    fun deleteChannel(channel: ChannelModel)
    fun getChannel(name: String): ChannelModel?
    fun getAllChannels(): ObjectSet<ChannelModel>


    companion object {
        val INSTANCE = requiredService<ChannelService>()
    }
}

val channelService get() = ChannelService.INSTANCE