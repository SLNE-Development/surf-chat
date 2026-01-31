package dev.slne.surf.chat.fallback.table

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.columns.time.offsetDateTime
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.ULongIdTable

object HistoryTable : ULongIdTable("chat_history") {
    val messageUuid = nativeUuid("message_uuid").uniqueIndex()
    val senderUuid = nativeUuid("sender_uuid")
    val receiverUuid = nativeUuid("receiver_uuid").nullable()
    val message = text("message")
    val sentAt = offsetDateTime("sent_at")
    val type = enumerationByName<MessageType>("type", 16)
    val server = char("server", 255)

    val deleted = bool("deleted").default(false)
    val deletedAt = offsetDateTime("deleted_at").nullable()
    val deletedBy = nativeUuid("deleted_by").nullable().default(null)
    val deletionReason = text("deletion_reason").nullable().default(null)
}