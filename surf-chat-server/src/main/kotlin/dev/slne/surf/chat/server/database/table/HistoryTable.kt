package dev.slne.surf.chat.server.database.table

import dev.slne.surf.chat.api.message.MessageType
import org.jetbrains.exposed.dao.id.IntIdTable

object HistoryTable : IntIdTable("chat_history") {
    val messageUuid = uuid("message_uuid").uniqueIndex()
    val senderUuid = uuid("sender_uuid")
    val receiverUuid = uuid("receiver_uuid").nullable()
    val message = text("message")
    val sentAt = long("sent_at")
    val type = enumeration<MessageType>("type")
    val server = varchar("server", 256)
    val channel = text("channel_name").nullable()
    val deletedBy = text("deleted_by").nullable()
}