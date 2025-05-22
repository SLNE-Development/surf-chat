package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.SpyService
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services
import org.bukkit.entity.Player

@AutoService(SpyService::class)
class BukkitSpyService : SpyService, Services.Fallback {
    val channelsSpys = Object2ObjectOpenHashMap<Channel, ObjectList<Player>>()
    val privateMessageSpys = Object2ObjectOpenHashMap<Player, ObjectList<Player>>()

    override fun getChannelSpys(channel: Channel): ObjectList<Player> {
        return channelsSpys.getOrDefault(channel, mutableObjectListOf())
    }

    override fun getPrivateMessageSpys(player: Player): ObjectList<Player> {
        return privateMessageSpys.getOrDefault(player, mutableObjectListOf())
    }

    override fun addChannelSpy(player: Player, channel: Channel) {
        channelsSpys.computeIfAbsent(channel) { mutableObjectListOf() }.add(player)
    }

    override fun removeChannelSpy(
        player: Player,
        channel: Channel
    ) {
        if (channelsSpys.containsKey(channel)) {
            channelsSpys[channel]?.remove(player)
        }
    }

    override fun addPrivateMessageSpy(player: Player, target: Player) {
        privateMessageSpys.computeIfAbsent(target) { mutableObjectListOf() }.add(player)
    }

    override fun removePrivateMessageSpy(player: Player, target: Player) {
        if (privateMessageSpys.containsKey(target)) {
            privateMessageSpys[target]?.remove(player)
        }
    }

    override fun hasChannelSpies(channel: Channel): Boolean {
        return channelsSpys.containsKey(channel) && channelsSpys[channel]?.isNotEmpty() == true
    }

    override fun hasPrivateMessageSpies(player: Player): Boolean {
        return privateMessageSpys.containsKey(player) && privateMessageSpys[player]?.isNotEmpty() == true
    }

    override fun isChannelSpying(player: Player): Boolean {
        return channelsSpys.values.any { it.contains(player) }
    }

    override fun isPrivateMessageSpying(player: Player): Boolean {
        return privateMessageSpys.containsKey(player) && privateMessageSpys[player]?.isNotEmpty() == true
    }

    override fun clearChannelSpys(player: Player) {
        channelsSpys.values.forEach { it.remove(player) }
        channelsSpys.keys.removeIf { channelsSpys[it]?.isEmpty() == true }
    }

    override fun clearPrivateMessageSpys(player: Player) {
        privateMessageSpys.values.forEach { it.remove(player) }
        privateMessageSpys.keys.removeIf { privateMessageSpys[it]?.isEmpty() == true }
    }

    override fun handleDisconnect(player: Player) {
        this.clearChannelSpys(player)
        this.clearPrivateMessageSpys(player)
    }
}