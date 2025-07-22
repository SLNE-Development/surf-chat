package dev.slne.surf.chat.api.entity

import dev.slne.surf.chat.api.model.Channel
import java.util.*

interface User {
    val name: String
    val uuid: UUID

    fun hasPermission(permission: String): Boolean

    fun configure(): ConfigurableUser
    fun channelMember(channel: Channel): ChannelMember?
}