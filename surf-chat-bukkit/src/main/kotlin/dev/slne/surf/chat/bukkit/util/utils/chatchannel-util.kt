package dev.slne.surf.chat.bukkit.util.utils

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.core.service.channelService

fun ChannelModel.edit(block: ChannelModel.() -> Unit): ChannelModel {
    channelService.unregister(this)
    block(this)
    channelService.register(this)
    return this
}

fun ChannelModel.handleLeave(user: ChatUserModel) {
    if (this.isOwner(user)) {
        var nextOwner = this.getModerators().firstOrNull()

        if (nextOwner == null) {
            nextOwner = this.getMembers(false).firstOrNull { it.uuid != user.uuid }
        }

        if (nextOwner == null) {
            this.leave(user)
            channelService.deleteChannel(this)

            user.sendPrefixed {
                info("Du hast den Nachrichtenkanal ")
                variableValue(this@handleLeave.name)
                info(" als letzter Spieler verlassen und der Kanal wurde gelöscht.")
            }
            return
        }

        this.transferOwnership(nextOwner)
        nextOwner.sendPrefixed {
            variableValue(user.getName())
            info(" hat den Nachrichtenkanal ")
            variableValue(this@handleLeave.name)
            info(" verlassen. Die Besitzerschaft wurde auf dich übertragen.")
        }
    }

    this.leave(user)
    user.sendPrefixed {
        success("Du hast den Nachrichtenkanal ")
        variableValue(this@handleLeave.name)
        success(" verlassen.")
    }
}