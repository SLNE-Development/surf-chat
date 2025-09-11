package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.fallback.table.NotifySettingsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class NotifySettingsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NotifySettingsEntity>(NotifySettingsTable)

    var userUuid by NotifySettingsTable.userUuid
    var pingsEnabled by NotifySettingsTable.pingsEnabled
    var invitesEnabled by NotifySettingsTable.invitesEnabled
}