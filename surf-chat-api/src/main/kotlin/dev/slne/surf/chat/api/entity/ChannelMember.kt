package dev.slne.surf.chat.api.entity

import dev.slne.surf.chat.api.model.ChannelRole

interface ChannelMember {
    val role: ChannelRole

    fun hasModeratorPermissions() = role == ChannelRole.OWNER || role == ChannelRole.MODERATOR
}