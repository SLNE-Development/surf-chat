package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.model.ChannelRole
import java.util.*

class FallbackChannelMember(
    override val uuid: UUID,
    override var role: ChannelRole
) : ChannelMember