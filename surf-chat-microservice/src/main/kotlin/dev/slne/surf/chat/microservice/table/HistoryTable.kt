package dev.slne.surf.chat.microservice.table

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.columns.time.offsetDateTime
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.ULongIdTable

object HistoryTable : ULongIdTable("chat_histories") {
    val messageUuid = nativeUuid("message_uuid").uniqueIndex()
    val senderUuid = nativeUuid("sender_uuid")
    val receiverUuid = nativeUuid("receiver_uuid").nullable()
    val message = text("message")
    val sentAt = offsetDateTime("sent_at")
    val type = varchar("type", 16).transform({ MessageType(it) }, { it.value })
    val server = char("server", 255)

    val deletedAt = offsetDateTime("deleted_at").nullable().default(null)
    val deletedBy = nativeUuid("deleted_by_uuid").nullable().default(null)
    val deletionReason = text("deletion_reason").nullable().default(null)

    init {
        index(false, senderUuid, sentAt)
        index(false, receiverUuid, sentAt)
        index(false, server, sentAt)
        index(false, senderUuid, type, sentAt)

        index(false, deletedAt)
        index(false, deletedBy)
    }
}