package dev.slne.surf.chat.server.table

import org.jetbrains.exposed.dao.id.IntIdTable

object FunctionalityTable : IntIdTable("chat_functionality") {
    val server = varchar("server", 256).uniqueIndex()
    val chatEnabled = bool("chat_enabled").default(true)
}