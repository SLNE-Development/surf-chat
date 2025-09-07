package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.core.entry.HistoryEntryImpl
import dev.slne.surf.chat.fallback.table.HistoryTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class HistoryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<HistoryEntity>(HistoryTable)

    var messageUuid by HistoryTable.messageUuid
    var senderUuid by HistoryTable.senderUuid
    var receiverUuid by HistoryTable.receiverUuid
    var message by HistoryTable.message
    var sentAt by HistoryTable.sentAt
    var type by HistoryTable.type
    var server by HistoryTable.server
    var channel by HistoryTable.channel
    var deletedBy by HistoryTable.deletedBy

    fun toDto() = HistoryEntryImpl(
        messageUuid,
        senderUuid,
        type,
        sentAt,
        message,
        server,
        deletedBy,
        receiverUuid,
        channel
    )
}