package dev.slne.surf.chat.server.database.repository

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.server.database.entity.IgnoreListEntity
import dev.slne.surf.chat.server.database.table.IgnoreListTable
import dev.slne.surf.cloud.api.common.util.mutableObjectSetOf
import dev.slne.surf.cloud.api.server.plugin.CoroutineTransactional
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@CoroutineTransactional
class IgnoreListRepository {
    suspend fun cacheIgnorelist(uuid: UUID) {
        SyncValues.ignoreList.removeIf { it.first == uuid }

        val ignoreList = mutableObjectSetOf<IgnoreListEntry>()

        IgnoreListEntity.find(IgnoreListTable.userUuid eq uuid).forEach {
            ignoreList.add(
                IgnoreListEntry(
                    it.userUuid,
                    it.userName,
                    it.targetUuid,
                    it.targetName,
                    it.createdAt
                )
            )
        }

        SyncValues.ignoreList.add(uuid to ignoreList)
    }

    suspend fun storeIgnorelist(uuid: UUID) {
        val ignoreList = SyncValues.ignoreList.firstOrNull { it.first == uuid }?.second ?: return

        IgnoreListEntity.find { IgnoreListTable.userUuid eq uuid }.forEach { it.delete() }

        ignoreList.forEach {
            IgnoreListEntity.new {
                userUuid = it.user
                userName = it.name
                targetUuid = it.target
                targetName = it.targetName
                createdAt = it.createdAt
            }
        }
    }
}