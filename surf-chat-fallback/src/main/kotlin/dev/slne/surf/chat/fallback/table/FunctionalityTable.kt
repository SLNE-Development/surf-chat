package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.sql.Table

object FunctionalityTable : Table("chat_functionality") {
    val server = varchar("server", 256)
    val chatEnabled = bool("chat_enabled").default(true)

    override val primaryKey = PrimaryKey(server)
}