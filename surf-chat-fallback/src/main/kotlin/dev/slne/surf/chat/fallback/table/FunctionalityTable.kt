package dev.slne.surf.chat.fallback.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.ULongIdTable

object FunctionalityTable : ULongIdTable("chat_functionalities") {
    val server = varchar("server", 256).uniqueIndex()
    val chatEnabled = bool("chat_enabled").default(true)
}