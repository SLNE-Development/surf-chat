package dev.slne.surf.chat.bukkit.util.utils

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.core.service.channelService

fun ChannelModel.edit(block: ChannelModel.() -> Unit): ChannelModel {
    channelService.unregister(this)
    block(this)
    channelService.register(this)
    return this
}