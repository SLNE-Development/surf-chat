package dev.slne.surf.chat.server.entity

import dev.slne.surf.chat.server.table.FunctionalityTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class FunctionalityEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FunctionalityEntity>(FunctionalityTable)

    var server by FunctionalityTable.server
    var chatEnabled by FunctionalityTable.chatEnabled
}