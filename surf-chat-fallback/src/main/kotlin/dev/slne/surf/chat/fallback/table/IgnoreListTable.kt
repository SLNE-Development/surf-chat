package dev.slne.surf.chat.fallback.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable

object IgnoreListTable : AuditableLongIdTable("chat_ignorelist") {
    val userUuid = nativeUuid("user_id")
    val ignoredUuid = nativeUuid("target_uuid")

    init {
        uniqueIndex(userUuid, ignoredUuid)
    }
}