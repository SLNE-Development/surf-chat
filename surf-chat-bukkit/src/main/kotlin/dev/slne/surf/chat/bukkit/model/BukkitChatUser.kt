package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.bukkit.util.utils.toPlayer
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

class BukkitChatUser(
    override val uuid: UUID,
    override val ignoreList: ObjectSet<UUID> = mutableObjectSetOf(),
    override var pmDisabled: Boolean = false,
    override var soundEnabled: Boolean = true,
    override var channelInvites: Boolean = true
) : ChatUser {
    override fun toggleChannelInvites(): Boolean {
        channelInvites = !channelInvites
        return channelInvites
    }

    override fun isIgnoring(target: UUID): Boolean {
        return ignoreList.contains(target)
    }

    override fun ignore(target: UUID) {
        ignoreList.add(target)
    }

    override fun stopIgnoring(target: UUID) {
        ignoreList.remove(target)
    }

    override fun toggleIgnore(target: UUID): Boolean {
        if (ignoreList.contains(target)) {
            ignoreList.remove(target)
            return false
        } else {
            ignoreList.add(target)
            return true
        }
    }

    override fun toggleSound(): Boolean {
        soundEnabled = !soundEnabled
        return soundEnabled
    }

    override fun acceptInvite(channel: Channel) {
        channel.join(this)
        channel.invites.remove(this)
    }

    override fun declineInvite(channel: Channel) {
        channel.invites.remove(this)
    }

    override fun hasOpenPms(): Boolean {
        return pmDisabled
    }

    override fun togglePm(): Boolean {
        pmDisabled = !pmDisabled
        return pmDisabled
    }

    override fun moveToChannel(channel: Channel) {
        val player = this.toPlayer() ?: return

        channelService.move(player, channel)
    }

    override fun getName(): String {
        return databaseService.getName(this.uuid)
    }
}