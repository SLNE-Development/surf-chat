package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.service.IgnoreService
import dev.slne.surf.chat.fallback.entity.IgnoreListEntity
import dev.slne.surf.chat.fallback.table.IgnoreListTable
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toMutableObjectSet
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteIgnoreWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@AutoService(IgnoreService::class)
class FallbackIgnoreService : IgnoreService, Services.Fallback {
    val ignorelist = mutableObject2ObjectMapOf<UUID, List<IgnoreListEntry>>()

    override fun createTable() {
        transaction {
            SchemaUtils.create(IgnoreListTable)
        }
    }

    override fun ignore(
        player: UUID,
        playerName: String,
        target: UUID,
        targetPlayerName: String
    ) {
        ignorelist[player] = ignorelist[player]?.plus(
            IgnoreListEntry(
                player, playerName,
                target,
                targetPlayerName,
                System.currentTimeMillis()
            )
        ) ?: listOf(
            IgnoreListEntry(
                player, playerName,
                target,
                targetPlayerName,
                System.currentTimeMillis()
            )
        )
    }

    override fun unIgnore(player: UUID, target: UUID) {
        ignorelist[player] = ignorelist[player]?.filterNot {
            it.target == target
        } ?: listOf()
    }

    override fun isIgnored(player: UUID, target: UUID): Boolean {
        return ignorelist[player]?.any {
            it.target == target
        } ?: false
    }

    override suspend fun cacheIgnorelist(player: UUID): ObjectSet<IgnoreListEntry> =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntity.find { IgnoreListTable.userUuid eq player }.map {
                it.toDto()
            }.toObjectSet()
        }.also {
            ignorelist[player] = it.toList()
        }

    override fun getIgnorelist(player: UUID): ObjectSet<IgnoreListEntry> {
        return ignorelist[player]?.toMutableObjectSet() ?: mutableObjectSetOf()
    }

    override suspend fun saveIgnorelist(
        player: UUID,
        ignorelist: ObjectSet<IgnoreListEntry>
    ) {
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListTable.deleteIgnoreWhere { IgnoreListTable.userUuid eq player }

            ignorelist.forEach { entry ->
                IgnoreListEntity.new {
                    userUuid = entry.user
                    userName = entry.name
                    targetUuid = entry.target
                    targetName = entry.targetName
                    createdAt = entry.createdAt
                }
            }
        }
    }
}