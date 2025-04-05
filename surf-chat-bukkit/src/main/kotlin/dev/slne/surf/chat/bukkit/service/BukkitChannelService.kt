package dev.slne.surf.chat.bukkit.service

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.type.ChannelRoleType
import dev.slne.surf.chat.bukkit.model.BukkitChannel
import dev.slne.surf.chat.bukkit.util.edit
import dev.slne.surf.chat.core.service.ChannelService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class BukkitChannelService(): ChannelService {
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
        if(player is OfflinePlayer) {
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

    override fun move(player: ChatUserModel, channel: ChannelModel) {
        TODO("Not yet implemented")
    }
}