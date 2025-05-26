package dev.slne.surf.chat.core.service.util

import net.kyori.adventure.chat.SignedMessage

data class HistoryEntry (
    val signature: SignedMessage.Signature,
    val message: String
    )