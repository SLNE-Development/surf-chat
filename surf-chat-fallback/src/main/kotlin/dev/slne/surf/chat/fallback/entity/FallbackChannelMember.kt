package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.api.entity.ChannelMember
import java.util.*

class FallbackChannelMember(
    override val uuid: UUID,
    override val name: String,
    override var role: ChannelRole
) : ChannelMember