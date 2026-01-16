package dev.slne.surf.chat.fallback.table

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object HistoryTable : LongIdTable("chat_history") {
    val messageUuid = uuid("message_uuid").uniqueIndex()
    val senderUuid = uuid("sender_uuid")
    val receiverUuid = uuid("receiver_uuid").nullable()
    val message = text("message")
    val sentAt = long("sent_at")
    val type = enumeration<MessageType>("type")
    val server = text("server")
    val deletedBy = text("deleted_by").nullable()
}