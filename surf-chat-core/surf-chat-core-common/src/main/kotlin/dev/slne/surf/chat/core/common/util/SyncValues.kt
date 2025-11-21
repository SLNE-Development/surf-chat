package dev.slne.surf.chat.core.common.util

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.cloud.api.common.sync.SyncValue
import java.util.*

typealias NameAndUuid = Pair<String, UUID>

object SyncValues {
    val denylistEntries: SyncSet<DenylistEntry> = SyncSet.Companion("chat:denylist:entries")
    val denylistActions: SyncSet<DenylistAction> = SyncSet.Companion("chat:denylist:actions")

    val chatFunctionalities: SyncSet<Pair<String, Boolean>> =
        SyncSet.Companion("chat:functionalities")
    val latestPrivateMessages: SyncSet<Pair<UUID, UUID>> = SyncSet.Companion("chat:private:target")
    val ignoreList = SyncSet<Pair<UUID, Set<IgnoreListEntry>>>("chat:ignorelist")

    val allowedDomains = SyncSet.Companion<String>("chat:filter:domains")
    val spamInterval = SyncValue.Companion<Long>("chat:filter:spam_interval", 10000L)
    val spamAmount = SyncValue.Companion<Int>("chat:filter:spam_amount", 5)
}