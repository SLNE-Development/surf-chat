package dev.slne.surf.chat.core.service

import java.util.UUID

interface ChannelService {
    fun getChannel(channelUuid: UUID)
}