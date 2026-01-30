package dev.slne.surf.chat.fallback.table

import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object DenylistActionsTable : LongIdTable("chat_denylist_actions") {
    val name = char("name", 64).uniqueIndex()
    val actionType = enumerationByName<DenylistActionType>("action_type", 16)
    val reason = largeText("reason")
    val duration = long("duration")
}