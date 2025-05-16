package dev.slne.surf.chat.bukkit.service

import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.type.ChannelRoleType
import dev.slne.surf.chat.bukkit.model.BukkitChannel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.utils.edit
import dev.slne.surf.chat.bukkit.util.utils.handleLeave
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.bukkit.util.utils.toChatUser
import dev.slne.surf.chat.core.service.ChannelService
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@AutoService(ChannelService::class)
class BukkitChannelService() : ChannelService, Fallback {
    private val channels = ObjectArraySet<ChannelModel>()
    override fun createChannel(name: String, owner: ChatUserModel): ChannelModel {
        val channel = BukkitChannel(name)

        channel.edit {
            members[owner] = ChannelRoleType.OWNER
        }

        this.register(channel)
        return channel
    }

    override fun deleteChannel(channel: ChannelModel) {
        channels.remove(channel)
    }

    override fun getChannel(name: String): ChannelModel? {
        return channels.find { it.name == name }
    }

    override fun getChannel(player: CommandSender): ChannelModel? {
        if (player is OfflinePlayer) {
            return channels.find { it -> it.members.keys.any { it.uuid == player.uniqueId } }
        }

        return null
    }

    override fun getAllChannels(): ObjectSet<ChannelModel> {
        return channels
    }

    override fun register(channel: ChannelModel) {
        channels.add(channel)
    }

    override fun unregister(channel: ChannelModel) {
        channels.remove(channel)
    }

    override fun move(player: Player, channel: ChannelModel) {
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