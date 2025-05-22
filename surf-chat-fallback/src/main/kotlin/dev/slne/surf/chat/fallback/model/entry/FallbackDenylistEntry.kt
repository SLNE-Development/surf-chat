package dev.slne.surf.chat.fallback.model.entry

import dev.slne.surf.chat.api.model.DenyListEntry

data class FallbackDenylistEntry(
    override val word: String,
    override val reason: String,
    override val addedAt: Long,
    override val addedBy: String
) : DenyListEntry  {
}