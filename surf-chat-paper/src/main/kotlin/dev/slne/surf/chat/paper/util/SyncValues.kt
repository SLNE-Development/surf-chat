package dev.slne.surf.chat.paper.util

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.cloud.api.common.sync.SyncSet
import java.util.*

object SyncValues {
    val denylistEntries: SyncSet<DenylistEntry> = SyncSet("chat:denylist:entries")
    val denylistActions: SyncSet<DenylistAction> = SyncSet("chat:denylist:actions")

    val chatFunctionalities: SyncSet<Pair<String, Boolean>> = SyncSet("chat:functionalities")
    val latestPrivateMessages: SyncSet<Pair<UUID, UUID>> = SyncSet("chat:private:target")

    fun init() {}
}