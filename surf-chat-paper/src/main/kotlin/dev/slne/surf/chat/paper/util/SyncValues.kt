package dev.slne.surf.chat.paper.util

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.cloud.api.common.sync.SyncSet

object SyncValues {
    val denylistEntries: SyncSet<DenylistEntry> = SyncSet("chat:denylist:entries")
    val denylistActions: SyncSet<DenylistAction> = SyncSet("chat:denylist:actions")

    fun init() {}
}