package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object IgnoreListTable : IntIdTable("chat_ignorelist") {
    val userUuid =
        varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val userName = varchar("user_name", 16).default("Error")
    val targetUuid =
        varchar("target_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val targetName = varchar("target_name", 16).default("Error")
    val createdAt = long("created_at")
}