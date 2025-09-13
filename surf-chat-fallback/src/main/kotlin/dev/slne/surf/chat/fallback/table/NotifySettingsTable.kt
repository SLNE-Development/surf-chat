package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable

object NotifySettingsTable : IntIdTable("chat_settings_notify") {
    val userUuid = uuid("user_uuid").uniqueIndex()
    val pingsEnabled = bool("pings_enabled").default(true)
    val invitesEnabled = bool("invites_enabled").default(true)
}