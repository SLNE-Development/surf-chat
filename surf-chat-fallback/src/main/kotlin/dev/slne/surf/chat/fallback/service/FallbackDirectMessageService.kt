package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.DirectMessageService
import dev.slne.surf.chat.fallback.entity.DMSettingsEntity
import dev.slne.surf.chat.fallback.table.DMSettingsTable
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.util.*

@AutoService(DirectMessageService::class)
class FallbackDirectMessageService : DirectMessageService, Services.Fallback {
    override fun createTable() {
        transaction {
            SchemaUtils.create(DMSettingsTable)
        }
    }

    override suspend fun directMessagesEnabled(uuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            DMSettingsEntity.find { DMSettingsTable.userUuid eq uuid }.firstOrNull()
                ?.toDto()?.directMessagesEnabled ?: true
        }

    override suspend fun enableDirectMessages(uuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            DMSettingsTable.upsert {
                it[userUuid] = uuid
                it[directMessagesEnabled] = true
            }
            return@newSuspendedTransaction
        }

    override suspend fun disableDirectMessages(uuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            DMSettingsTable.upsert {
                it[userUuid] = uuid
                it[directMessagesEnabled] = false
            }
            return@newSuspendedTransaction
        }
}