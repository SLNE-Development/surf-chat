package dev.slne.surf.chat.paper.spy

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Service
import java.util.*

@Service
class SpyService {
    val channelsSpies = mutableObject2ObjectMapOf<Channel, ObjectList<UUID>>()
    val privateMessageSpies = mutableObject2ObjectMapOf<UUID, ObjectList<UUID>>()

    fun getChannelSpies(channel: Channel) =
        channelsSpies.get(channel) ?: mutableObjectListOf()

    fun getPrivateMessageSpies(player: UUID) =
        privateMessageSpies.get(player) ?: mutableObjectListOf()

    fun addChannelSpy(
        player: UUID,
        channel: Channel
    ) = channelsSpies.computeIfAbsent(channel) { mutableObjectListOf() }.add(player)

    fun removeChannelSpy(
        player: UUID,
        channel: Channel
    ) = channelsSpies[channel]?.remove(player) ?: false

    fun addPrivateMessageSpy(player: UUID, target: UUID) =
        privateMessageSpies.computeIfAbsent(target) { mutableObjectListOf() }.add(player)

    fun removePrivateMessageSpy(player: UUID, target: UUID) =
        privateMessageSpies[target]?.remove(player) ?: false

    fun hasChannelSpies(channel: Channel) =
        channelsSpies.containsKey(channel) && channelsSpies[channel]?.isNotEmpty() == true

    fun hasPrivateMessageSpies(player: UUID) =
        privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true

    fun isChannelSpying(player: UUID) = channelsSpies.values.any { it.contains(player) }
    fun isPrivateMessageSpying(player: UUID) =
        privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true

    fun clearChannelSpies(player: UUID) {
        channelsSpies.values.forEach { it.remove(player) }
        channelsSpies.keys.removeIf { channelsSpies[it]?.isEmpty() == true }
    }

    fun clearPrivateMessageSpies(player: UUID) {
        privateMessageSpies.values.forEach { it.remove(player) }
        privateMessageSpies.keys.removeIf { privateMessageSpies[it]?.isEmpty() == true }
    }

    fun cleanup(player: UUID) {
        this.clearChannelSpies(player)
        this.clearPrivateMessageSpies(player)
    }
}

val spyService get() = ChatContextHolderImpl.instance.context.getBean<SpyService>()