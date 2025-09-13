package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.service.IgnoreService
import dev.slne.surf.chat.fallback.entity.IgnoreListEntity
import dev.slne.surf.chat.fallback.table.IgnoreListTable
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@AutoService(IgnoreService::class)
class FallbackIgnoreService : IgnoreService, Services.Fallback {
    override fun createTable() {
        transaction {
            SchemaUtils.create(IgnoreListTable)
        }
    }

    override suspend fun ignore(
        player: UUID,
        playerName: String,
        target: UUID,
        targetPlayerName: String
    ) =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntity.new {
                userUuid = player
                userName = playerName
                targetUuid = target
                targetName = targetPlayerName
                createdAt = System.currentTimeMillis()
            }
            return@newSuspendedTransaction
        }

    override suspend fun unIgnore(player: UUID, target: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntity.find {
                (IgnoreListTable.userUuid eq player) and (IgnoreListTable.targetUuid eq target)
            }.forEach {
                it.delete()
            }
            return@newSuspendedTransaction
        }

    override suspend fun isIgnored(player: UUID, target: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntity.find {
                (IgnoreListTable.userUuid eq player) and (IgnoreListTable.targetUuid eq target)
            }.firstOrNull() != null
        }

    override suspend fun getIgnoreList(player: UUID): ObjectSet<IgnoreListEntry> =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntity.find { IgnoreListTable.userUuid eq player }.map {
                it.toDto()
            }.toObjectSet()
        }
}