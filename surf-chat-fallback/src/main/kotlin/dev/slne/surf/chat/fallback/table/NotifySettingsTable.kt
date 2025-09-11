package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object NotifySettingsTable : IntIdTable("chat_settings_notify") {
    val userUuid =
        varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val pingsEnabled = bool("pings_enabled").default(true)
    val invitesEnabled = bool("invites_enabled").default(true)
}