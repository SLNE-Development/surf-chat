package dev.slne.surf.chat.core.client

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.cloud.api.common.sync.SyncValue
import java.util.*

object SyncValues {
    val denylistEntries: SyncSet<DenylistEntry> = SyncSet("chat:denylist:entries")
    val denylistActions: SyncSet<DenylistAction> = SyncSet("chat:denylist:actions")

    val chatFunctionalities: SyncSet<Pair<String, Boolean>> =
        SyncSet("chat:functionalities")
    val latestPrivateMessages: SyncSet<Pair<UUID, UUID>> = SyncSet("chat:private:target")

    val allowedDomains = SyncSet<String>("chat:filter:domains")
    val spamInterval = SyncValue<Long>("chat:filter:spam_interval", 10000L)
    val spamAmount = SyncValue<Int>("chat:filter:spam_amount", 5)
}