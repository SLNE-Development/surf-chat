package dev.slne.surf.chat.fallback.table

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import org.jetbrains.exposed.dao.id.IntIdTable

object HistoryTable : IntIdTable("chat_history") {
    val messageUuid = uuid("message_uuid").uniqueIndex()
    val senderUuid = uuid("sender_uuid")
    val receiverUuid = uuid("receiver_uuid").nullable()
    val message = text("message")
    val sentAt = long("sent_at")
    val type = enumeration<MessageType>("type")
    val server = text("server").transform({ ChatServer.of(it) }, { it.internalName })
    val channel = text("channel_name").nullable()
    val deletedBy = text("deleted_by").nullable()
}