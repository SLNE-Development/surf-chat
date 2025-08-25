package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.sql.Table
import java.util.*

object NotifySettingsTable : Table("chat_settings_notify") {
    val userUuid =
        varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val pingsEnabled = bool("pings_enabled").default(true)
    val invitesEnabled = bool("invites_enabled").default(true)

    override val primaryKey = PrimaryKey(userUuid)
}