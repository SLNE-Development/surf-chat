package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.core.entry.DenylistActionImpl
import dev.slne.surf.chat.fallback.table.DenylistActionsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DenylistActionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DenylistActionEntity>(DenylistActionsTable)

    var name by DenylistActionsTable.name
    var actionType by DenylistActionsTable.actionType
    var reason by DenylistActionsTable.reason
    var duration by DenylistActionsTable.duration

    fun toDto() = DenylistActionImpl(name, actionType, reason, duration)
}