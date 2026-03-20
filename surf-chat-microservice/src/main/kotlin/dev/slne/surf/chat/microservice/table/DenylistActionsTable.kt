package dev.slne.surf.chat.microservice.table

import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.ULongIdTable

object DenylistActionsTable : ULongIdTable("chat_denylist_actions") {
    val name = char("name", 255).uniqueIndex()
    val actionType = enumerationByName<DenylistActionType>("action_type", 16)
    val reason = largeText("reason")
    val duration = long("duration")
}