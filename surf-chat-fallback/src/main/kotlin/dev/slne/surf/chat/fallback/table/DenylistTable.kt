package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.sql.Table

object DenylistTable : Table("chat_denylist") {
    val index = integer("index").autoIncrement().uniqueIndex()
    val word = varchar("word", 255).uniqueIndex()
    val reason = text("reason")
    val addedBy = varchar("added_by", 16)
    val addedAt = long("added_at")

    override val primaryKey = PrimaryKey(index)
}