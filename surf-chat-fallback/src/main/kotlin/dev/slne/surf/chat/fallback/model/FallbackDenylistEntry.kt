package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistEntry

data class FallbackDenylistEntry(
    override val word: String,
    override val reason: String,
    override val addedBy: String,
    override val addedAt: Long,
    override val action: DenylistAction
) : DenylistEntry