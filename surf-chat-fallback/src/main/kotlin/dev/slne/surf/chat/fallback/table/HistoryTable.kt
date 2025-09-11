package dev.slne.surf.chat.fallback.table

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object HistoryTable : IntIdTable("chat_history") {
    val messageUuid =
        varchar("message_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val senderUuid =
        varchar("sender_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val receiverUuid =
        varchar("receiver_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
            .nullable()
    val message = text("message")
    val sentAt = long("sent_at")
    val type = text("type").transform({ MessageType.valueOf(it) }, { it.name })
    val server = text("server").transform({ ChatServer.of(it) }, { it.internalName })
    val channel = text("channel_name").nullable()
    val deletedBy = text("deleted_by").nullable()
}