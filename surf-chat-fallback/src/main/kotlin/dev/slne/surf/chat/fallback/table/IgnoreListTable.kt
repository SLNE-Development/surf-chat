package dev.slne.surf.chat.fallback.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object IgnoreListTable : LongIdTable("chat_ignorelist") {
    val userId = long("user_id").references(UserTable.id)
    val targetUuid = uuid("target_uuid")
    val targetName = varchar("target_name", 16)
    val createdAt = long("created_at")
}