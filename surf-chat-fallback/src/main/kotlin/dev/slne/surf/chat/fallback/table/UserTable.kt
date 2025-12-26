package dev.slne.surf.chat.fallback.table

import org.jetbrains.exposed.dao.id.LongIdTable

object UserTable : LongIdTable("chat_users") {
    val uuid = uuid("uuid").uniqueIndex()
    val name = varchar("name", 16)
    val directMessagesEnabled = bool("direct_messages_enabled").default(true)
    val channelInviteMessagesEnabled = bool("channel_invite_messages_enabled").default(true)
    val chatPingsEnabled = bool("chat_pings_enabled").default(true)
}