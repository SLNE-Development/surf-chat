package dev.slne.surf.chat.api.util.history

import java.util.UUID

data class HistoryPair(
    val messageID: UUID,
    val sendTime: Long
)

