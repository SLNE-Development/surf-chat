package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.fallback.table.DMSettingsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class DMSettingsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DMSettingsEntity>(DMSettingsTable)

    var userUuid by DMSettingsTable.userUuid
    var directMessagesEnabled by DMSettingsTable.directMessagesEnabled

    fun toDto() = DmSettingsDto(
        userUuid = userUuid,
        directMessagesEnabled = directMessagesEnabled
    )
}

data class DmSettingsDto(
    val userUuid: UUID,
    val directMessagesEnabled: Boolean
)