package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable

object IgnoreListTable : IntIdTable("chat_ignorelist") {
    val userUuid =
        uuid("user_uuid")
    val userName = varchar("user_name", 16).default("Error")
    val targetUuid =
        uuid("target_uuid")
    val targetName = varchar("target_name", 16).default("Error")
    val createdAt = long("created_at")
}