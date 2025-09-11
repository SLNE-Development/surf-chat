package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable

object DenylistTable : IntIdTable("chat_denylist_entries") {
    val word = varchar("word", 255).uniqueIndex()
    val reason = text("reason")
    val addedBy = varchar("added_by", 16)
    val addedAt = long("added_at")
    val action = reference("action_id", DenylistActionsTable)
}