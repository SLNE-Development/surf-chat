package dev.slne.surf.chat.api.entity

import dev.slne.surf.chat.api.model.ChannelRole
import java.util.*

interface ChannelMember {
    val uuid: UUID
    val name: String
    var role: ChannelRole

    fun hasModeratorPermissions() = role == ChannelRole.OWNER || role == ChannelRole.MODERATOR
}