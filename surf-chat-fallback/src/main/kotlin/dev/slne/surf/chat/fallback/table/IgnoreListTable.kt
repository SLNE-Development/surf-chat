package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable

object IgnoreListTable : IntIdTable("chat_ignorelist") {
    val userId = long("user_id").references(UserTable.id)
    val targetUuid = uuid("target_uuid")
    val targetName = varchar("target_name", 16)
    val createdAt = long("created_at")
}