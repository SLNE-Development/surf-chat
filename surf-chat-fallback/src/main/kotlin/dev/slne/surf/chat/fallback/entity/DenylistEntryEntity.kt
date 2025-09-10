package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.fallback.model.FallbackDenylistEntry
import dev.slne.surf.chat.fallback.table.DenylistTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DenylistEntryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DenylistEntryEntity>(DenylistTable)

    var word by DenylistTable.word
    var reason by DenylistTable.reason
    var addedBy by DenylistTable.addedBy
    var addedAt by DenylistTable.addedAt
    var action by DenylistActionEntity referencedOn DenylistTable.action

    fun toDto() = FallbackDenylistEntry(
        word,
        reason,
        addedBy,
        addedAt,
        action.toDto()
    )
}