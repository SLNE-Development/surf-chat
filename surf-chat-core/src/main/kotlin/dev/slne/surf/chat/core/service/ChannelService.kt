package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

interface ChannelService {
    fun createChannel(name: String, owner: ChatUserModel): ChannelModel
    fun deleteChannel(channel: ChannelModel)
    fun getChannel(name: String): ChannelModel?
    fun getChannel(player: OfflinePlayer): ChannelModel?
    fun getAllChannels(): ObjectSet<ChannelModel>

    fun register(channel: ChannelModel)
    fun unregister(channel: ChannelModel)


    companion object {
        val INSTANCE = requiredService<ChannelService>()
    }
}

val channelService get() = ChannelService.INSTANCE