package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.entity.Player
import java.util.UUID

interface SpyService {
    fun getChannelSpies(channel: ChannelModel): ObjectList<UUID>
    fun getPrivateMessageSpies(player: UUID): ObjectList<UUID>

    fun addChannelSpy(player: UUID, channel: ChannelModel)
    fun removeChannelSpy(player: UUID, channel: ChannelModel)

    fun addPrivateMessageSpy(player: UUID, target: UUID)
    fun removePrivateMessageSpy(player: UUID, target: UUID)

    fun hasChannelSpies(channel: ChannelModel): Boolean
    fun hasPrivateMessageSpies(player: UUID): Boolean

    fun isChannelSpying(player: UUID): Boolean
    fun isPrivateMessageSpying(player: UUID): Boolean

    fun clearChannelSpies(player: UUID)
    fun clearPrivateMessageSpies(player: UUID)

    fun handleDisconnect(player: UUID)

    companion object {
        val INSTANCE = requiredService<SpyService>()
    }
}

val spyService get() = SpyService.INSTANCE