package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.entity.Player

interface SpyService {
    fun getChannelSpys(channel: ChannelModel): ObjectList<Player>
    fun getPrivateMessageSpys(player: Player): ObjectList<Player>

    fun addChannelSpy(player: Player, channel: ChannelModel)
    fun removeChannelSpy(player: Player, channel: ChannelModel)

    fun addPrivateMessageSpy(player: Player, target: Player)
    fun removePrivateMessageSpy(player: Player, target: Player)

    fun hasChannelSpys(channel: ChannelModel): Boolean
    fun hasPrivateMessageSpys(player: Player): Boolean

    fun isChannelSpying(player: Player): Boolean
    fun isPrivateMessageSpying(player: Player): Boolean

    fun clearChannelSpys(player: Player)
    fun clearPrivateMessageSpys(player: Player)

    fun handleDisconnect(player: Player)

    companion object {
        val INSTANCE = requiredService<SpyService>()
    }
}

val spyService get() = SpyService.INSTANCE