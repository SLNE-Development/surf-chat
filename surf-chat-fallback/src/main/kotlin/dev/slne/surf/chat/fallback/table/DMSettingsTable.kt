package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object DMSettingsTable : IntIdTable("chat_settings_dm") {
    val userUuid =
        varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val directMessagesEnabled = bool("direct_messages_enabled").default(true)
}