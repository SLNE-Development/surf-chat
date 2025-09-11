package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable

object FunctionalityTable : IntIdTable("chat_functionality") {
    val server = varchar("server", 256)
    val chatEnabled = bool("chat_enabled").default(true)
}