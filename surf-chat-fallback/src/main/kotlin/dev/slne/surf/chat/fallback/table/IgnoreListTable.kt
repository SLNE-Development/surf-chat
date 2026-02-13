package dev.slne.surf.chat.fallback.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable

object IgnoreListTable : AuditableLongIdTable("chat_ignorelist_entries") {
    val userUuid = nativeUuid("user_id").index()
    val ignoredUuid = nativeUuid("target_uuid")

    init {
        uniqueIndex(userUuid, ignoredUuid)
    }
}