package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.channel.ChannelRole
import java.util.UUID

class FallbackChannelMember(
    override val uuid: UUID,
    override val name: String,
    override var role: ChannelRole
) : ChannelMember {
}