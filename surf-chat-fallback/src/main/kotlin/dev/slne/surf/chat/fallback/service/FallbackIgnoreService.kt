package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.service.IgnoreService
import dev.slne.surf.chat.fallback.model.FallbackIgnoreListEntry
import dev.slne.surf.chat.fallback.table.IgnoreListTable
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
            IgnoreListTable.insert {
                it[userUuid] = player
                it[userName] = playerName
                it[targetUuid] = target
                it[targetName] = targetPlayerName
                it[createdAt] = System.currentTimeMillis()
            }
            return@newSuspendedTransaction
        }

    override suspend fun unIgnore(player: UUID, target: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListTable.deleteWhere {
                (userUuid eq player) and (targetUuid eq target)
            }
            return@newSuspendedTransaction
        }

    override suspend fun isIgnored(player: UUID, target: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListTable.selectAll()
                .where((IgnoreListTable.userUuid eq player) and (IgnoreListTable.targetUuid eq target))
                .any()
        }

    override suspend fun getIgnoreList(player: UUID): ObjectSet<IgnoreListEntry> =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListTable.selectAll().where(
                IgnoreListTable.userUuid eq player
            ).map {
                FallbackIgnoreListEntry(
                    user = it[IgnoreListTable.userUuid],
                    name = it[IgnoreListTable.userName],
                    target = it[IgnoreListTable.targetUuid],
                    targetName = it[IgnoreListTable.targetName],
                    createdAt = it[IgnoreListTable.createdAt]
                )
            }.toObjectSet()

        }
}