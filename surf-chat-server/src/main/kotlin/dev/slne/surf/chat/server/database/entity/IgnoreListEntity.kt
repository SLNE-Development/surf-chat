package dev.slne.surf.chat.server.database.entity

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.server.database.table.IgnoreListTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class IgnoreListEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<IgnoreListEntity>(IgnoreListTable)

    var userUuid by IgnoreListTable.userUuid
    var userName by IgnoreListTable.userName
    var targetUuid by IgnoreListTable.targetUuid
    var targetName by IgnoreListTable.targetName
    var createdAt by IgnoreListTable.createdAt

    fun toDto() = IgnoreListEntry(
        userUuid,
        userName,
        targetUuid,
        targetName,
        createdAt
    )
}