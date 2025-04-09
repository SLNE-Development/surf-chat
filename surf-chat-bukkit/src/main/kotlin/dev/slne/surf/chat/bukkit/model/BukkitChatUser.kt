package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.bukkit.util.toPlayer
import dev.slne.surf.chat.core.service.channelService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import org.bukkit.Bukkit
import java.util.*

class BukkitChatUser (
    override val uuid: UUID,
    override val ignoreList: ObjectArraySet<UUID> = ObjectArraySet(),
    override var pmToggled: Boolean = false
): ChatUserModel {
    override fun isIgnoring(target: UUID): Boolean {
        return ignoreList.contains(target)
    }

    override fun ignore(target: UUID) {
        ignoreList.add(target)
    }

    override fun unIgnore(target: UUID) {
        ignoreList.remove(target)
    }

    override fun toggleIgnore(target: UUID): Boolean {
        if(ignoreList.contains(target)) {
            ignoreList.remove(target)
            return false
        } else {
            ignoreList.add(target)
            return true
        }
    }

    override fun acceptInvite(channel: ChannelModel) {
        channel.join(this)
        channel.invites.remove(this)
    }

    override fun declineInvite(channel: ChannelModel) {
        channel.invites.remove(this)
    }

    override fun hasOpenPms(): Boolean {
        return pmToggled
    }

    override fun togglePm(): Boolean {
        pmToggled = !pmToggled
        return pmToggled
    }

    override fun moveToChannel(channel: ChannelModel) {
        val player = this.toPlayer() ?: return

        channelService.move(player, channel)
    }

    override fun getName(): String {
        return Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
    }
}