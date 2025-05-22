package dev.slne.surf.chat.bukkit.service

import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.model.ChatUser
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.bukkit.model.BukkitChannel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.utils.edit
import dev.slne.surf.chat.bukkit.util.utils.handleLeave
import dev.slne.surf.chat.bukkit.util.utils.toChatUser
import dev.slne.surf.chat.core.service.ChannelService
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@AutoService(ChannelService::class)
class BukkitChannelService() : ChannelService, Fallback {
    private val channels = ObjectArraySet<Channel>()
    override fun createChannel(name: String, owner: ChatUser): Channel {
        val channel = BukkitChannel(name)

        channel.edit {
            members[owner] = ChannelRole.OWNER
        }

        this.register(channel)
        return channel
    }

    override fun deleteChannel(channel: Channel) {
        channels.remove(channel)
    }

    override fun getChannel(name: String): Channel? {
        return channels.find { it.name == name }
    }

    override fun getChannel(player: CommandSender): Channel? {
        if (player is OfflinePlayer) {
            return channels.find { it -> it.members.keys.any { it.uuid == player.uniqueId } }
        }

        return null
    }

    override fun getAllChannels(): ObjectSet<Channel> {
        return channels
    }

    override fun register(channel: Channel) {
        channels.add(channel)
    }

    override fun unregister(channel: Channel) {
        channels.remove(channel)
    }

    override fun move(player: Player, channel: Channel) {
        val currentChannel = channelService.getChannel(player)

        plugin.launch {
            val user = player.toChatUser()

            if (currentChannel != null) {
                if (currentChannel == channel) {
                    return@launch
                }

                currentChannel.handleLeave(user)
            }

            channel.join(player.toChatUser())
        }
    }

    override fun handleDisconnect(player: Player) {
        val channel = channelService.getChannel(player) ?: return

        plugin.launch {
            channel.handleLeave(databaseService.getUser(player.uniqueId))
        }
    }
}