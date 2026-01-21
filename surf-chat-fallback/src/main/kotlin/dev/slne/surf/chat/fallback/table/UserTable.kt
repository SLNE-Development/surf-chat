package dev.slne.surf.chat.fallback.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object UserTable : LongIdTable("chat_users") {
    val uuid = nativeUuid("uuid").uniqueIndex()
    val name = varchar("name", 16)
}