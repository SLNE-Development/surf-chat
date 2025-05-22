package dev.slne.surf.chat.api.channel

import java.util.UUID

interface ChannelMember {
    val uuid: UUID
    val name: String

    var role: ChannelRole
}