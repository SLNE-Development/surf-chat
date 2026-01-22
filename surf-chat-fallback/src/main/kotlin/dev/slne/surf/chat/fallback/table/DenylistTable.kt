package dev.slne.surf.chat.fallback.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object DenylistTable : LongIdTable("chat_denylist_entries") {
    val word = varchar("word", 255).uniqueIndex()
    val reason = text("reason")
    val addedBy = varchar("added_by", 16)
    val addedAt = long("added_at")
    val action = long("action_id").references(DenylistActionsTable.id)
}