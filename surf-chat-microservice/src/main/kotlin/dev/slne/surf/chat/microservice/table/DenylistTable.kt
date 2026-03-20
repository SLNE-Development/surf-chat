package dev.slne.surf.chat.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable

object DenylistTable : AuditableLongIdTable("chat_denylist_entries") {
    val word = char("word", 255).uniqueIndex()
    val reason = text("reason")
    val addedBy = nativeUuid("added_by_uuid").nullable().default(null)
    val action = reference("action_id", DenylistActionsTable.id)
}