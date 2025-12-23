package dev.slne.surf.chat.core.util

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.User

fun User.channelMember(channel: Channel) = channel.members.find { it.uuid == this.uuid }