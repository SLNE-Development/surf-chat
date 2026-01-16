package dev.slne.surf.chat.fallback.table

import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object DenylistActionsTable : LongIdTable("chat_denylist_actions") {
    var name = varchar("name", 64).uniqueIndex()
    var actionType =
        varchar("action_type", 16).transform({ DenylistActionType.valueOf(it) }, { it.toString() })
    var reason = largeText("reason")
    var duration = long("duration")
}