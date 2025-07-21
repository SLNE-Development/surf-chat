package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.core.service.SpyService
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services
import org.bukkit.entity.Player
import java.util.UUID

@AutoService(SpyService::class)
class BukkitSpyService : SpyService, Services.Fallback {
    val channelsSpies = Object2ObjectOpenHashMap<ChannelModel, ObjectList<UUID>>()
    val privateMessageSpies = Object2ObjectOpenHashMap<UUID, ObjectList<UUID>>()

    override fun getChannelSpies(channel: ChannelModel): ObjectList<UUID> {
        return channelsSpies.getOrDefault(channel, mutableObjectListOf())
    }

    override fun getPrivateMessageSpies(player: UUID): ObjectList<UUID> {
        return privateMessageSpies.getOrDefault(player, mutableObjectListOf())
    }

    override fun addChannelSpy(player: UUID, channel: ChannelModel) {
        channelsSpies.computeIfAbsent(channel) { mutableObjectListOf() }.add(player)
    }

    override fun removeChannelSpy(
        player: UUID,
        channel: ChannelModel
    ) {
        if (channelsSpies.containsKey(channel)) {
            channelsSpies[channel]?.remove(player)
        }
    }

    override fun addPrivateMessageSpy(player: UUID, target: UUID) {
        privateMessageSpies.computeIfAbsent(target) { mutableObjectListOf() }.add(player)
    }

    override fun removePrivateMessageSpy(player: UUID, target: UUID) {
        if (privateMessageSpies.containsKey(target)) {
            privateMessageSpies[target]?.remove(player)
        }
    }

    override fun hasChannelSpies(channel: ChannelModel): Boolean {
        return channelsSpies.containsKey(channel) && channelsSpies[channel]?.isNotEmpty() == true
    }

    override fun hasPrivateMessageSpies(player: UUID): Boolean {
        return privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true
    }

    override fun isChannelSpying(player: UUID): Boolean {
        return channelsSpies.values.any { it.contains(player) }
    }

    override fun isPrivateMessageSpying(player: UUID): Boolean {
        return privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true
    }

    override fun clearChannelSpies(player: UUID) {
        channelsSpies.values.forEach { it.remove(player) }
        channelsSpies.keys.removeIf { channelsSpies[it]?.isEmpty() == true }
    }

    override fun clearPrivateMessageSpies(player: UUID) {
        privateMessageSpies.values.forEach { it.remove(player) }
        privateMessageSpies.keys.removeIf { privateMessageSpies[it]?.isEmpty() == true }
    }

    override fun handleDisconnect(player: UUID) {
        this.clearChannelSpies(player)
        this.clearPrivateMessageSpies(player)
    }
}