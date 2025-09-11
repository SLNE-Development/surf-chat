package dev.slne.surf.chat.core.entry

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistEntry

data class DenylistEntryImpl(
    override val word: String,
    override val reason: String,
    override val addedBy: String,
    override val addedAt: Long,
    override val action: DenylistAction
) : DenylistEntry