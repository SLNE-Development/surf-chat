package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.entity.ChannelMember
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

interface Channel {
    val channelUuid: UUID
    val channelName: String
    val members: ObjectSet<ChannelMember>
    val createdAt: Long
}