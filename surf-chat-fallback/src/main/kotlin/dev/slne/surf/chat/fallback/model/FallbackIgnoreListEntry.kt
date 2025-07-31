package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import java.util.*

class FallbackIgnoreListEntry(
    override val user: UUID,
    override val name: String,
    override val target: UUID,
    override val targetName: String,
    override val createdAt: Long
) : IgnoreListEntry