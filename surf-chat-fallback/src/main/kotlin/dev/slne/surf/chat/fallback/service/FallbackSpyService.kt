package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.core.service.SpyService
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SpyService::class)
class FallbackSpyService : SpyService, Services.Fallback {
    val channelsSpies = mutableObject2ObjectMapOf<Channel, ObjectList<UUID>>()
    val privateMessageSpies = mutableObject2ObjectMapOf<UUID, ObjectList<UUID>>()

    override fun getChannelSpies(channel: Channel) =
        channelsSpies.get(channel) ?: mutableObjectListOf()

    override fun getPrivateMessageSpies(player: UUID) =
        privateMessageSpies.get(player) ?: mutableObjectListOf()

    override fun addChannelSpy(
        player: UUID,
        channel: Channel
    ) = channelsSpies.computeIfAbsent(channel) { mutableObjectListOf() }.add(player)

    override fun removeChannelSpy(
        player: UUID,
        channel: Channel
    ) = channelsSpies[channel]?.remove(player) ?: false

    override fun addPrivateMessageSpy(player: UUID, target: UUID) =
        privateMessageSpies.computeIfAbsent(target) { mutableObjectListOf() }.add(player)

    override fun removePrivateMessageSpy(player: UUID, target: UUID) =
        privateMessageSpies[target]?.remove(player) ?: false

    override fun hasChannelSpies(channel: Channel) =
        channelsSpies.containsKey(channel) && channelsSpies[channel]?.isNotEmpty() == true

    override fun hasPrivateMessageSpies(player: UUID) =
        privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true

    override fun isChannelSpying(player: UUID) = channelsSpies.values.any { it.contains(player) }
    override fun isPrivateMessageSpying(player: UUID) =
        privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true

    override fun clearChannelSpies(player: UUID) {
        channelsSpies.values.forEach { it.remove(player) }
        channelsSpies.keys.removeIf { channelsSpies[it]?.isEmpty() == true }
    }

    override fun clearPrivateMessageSpies(player: UUID) {
        privateMessageSpies.values.forEach { it.remove(player) }
        privateMessageSpies.keys.removeIf { privateMessageSpies[it]?.isEmpty() == true }
    }

    override fun cleanup(player: UUID) {
        this.clearChannelSpies(player)
        this.clearPrivateMessageSpies(player)
    }
}