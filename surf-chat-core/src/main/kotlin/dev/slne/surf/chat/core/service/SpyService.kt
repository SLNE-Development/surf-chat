package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*


interface SpyService {
    fun getChannelSpies(channel: Channel): ObjectList<UUID>
    fun getPrivateMessageSpies(player: UUID): ObjectList<UUID>

    fun addChannelSpy(player: UUID, channel: Channel): Boolean
    fun removeChannelSpy(player: UUID, channel: Channel): Boolean

    fun addPrivateMessageSpy(player: UUID, target: UUID): Boolean
    fun removePrivateMessageSpy(player: UUID, target: UUID): Boolean

    fun hasChannelSpies(channel: Channel): Boolean
    fun hasPrivateMessageSpies(player: UUID): Boolean

    fun isChannelSpying(player: UUID): Boolean
    fun isPrivateMessageSpying(player: UUID): Boolean

    fun clearChannelSpies(player: UUID)
    fun clearPrivateMessageSpies(player: UUID)

    fun cleanup(player: UUID)

    companion object {
        val INSTANCE = requiredService<SpyService>()
    }
}

val spyService get() = SpyService.INSTANCE