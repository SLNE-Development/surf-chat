package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.service.IgnoreService
import dev.slne.surf.chat.fallback.model.FallbackIgnoreListEntry
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
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
    object IgnoreListEntries : Table("chat_ignore_list") {
        val userUuid =
            varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val targetUuid =
            varchar("target_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val createdAt = long("created_at")

        override val primaryKey = PrimaryKey(userUuid, targetUuid)
    }

    override fun createTable() {
        transaction {
            SchemaUtils.create(IgnoreListEntries)
        }
    }

    override suspend fun ignore(player: UUID, target: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntries.insert {
                it[userUuid] = player
                it[targetUuid] = target
                it[createdAt] = System.currentTimeMillis()
            }
            return@newSuspendedTransaction
        }

    override suspend fun unIgnore(player: UUID, target: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntries.deleteWhere {
                (userUuid eq player) and (targetUuid eq target)
            }
            return@newSuspendedTransaction
        }

    override suspend fun isIgnored(player: UUID, target: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntries.selectAll()
                .where((IgnoreListEntries.userUuid eq player) and (IgnoreListEntries.targetUuid eq target))
                .any()
        }

    override suspend fun getIgnoreList(player: UUID): ObjectSet<IgnoreListEntry> =
        newSuspendedTransaction(Dispatchers.IO) {
            IgnoreListEntries.selectAll().where(
                IgnoreListEntries.userUuid eq player
            ).map {
                FallbackIgnoreListEntry(
                    user = it[IgnoreListEntries.userUuid],
                    name = PlayerLookupService.getUsername(it[IgnoreListEntries.userUuid])
                        ?: "Error",
                    target = it[IgnoreListEntries.targetUuid],
                    targetName = PlayerLookupService.getUsername(it[IgnoreListEntries.targetUuid])
                        ?: "Error",
                    createdAt = it[IgnoreListEntries.createdAt]
                )
            }.toObjectSet()

        }
}