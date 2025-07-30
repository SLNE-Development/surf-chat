package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.entry.DenylistEntry

class FallbackDenylistEntry(
    override val word: String,
    override val reason: String,
    override val addedBy: String,
    override val addedAt: Long
) : DenylistEntry