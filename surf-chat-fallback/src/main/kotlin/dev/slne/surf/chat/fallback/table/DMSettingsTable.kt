package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable

object DMSettingsTable : IntIdTable("chat_settings_dm") {
    val userUuid = uuid("user_uuid").uniqueIndex()
    val directMessagesEnabled = bool("direct_messages_enabled").default(true)
}