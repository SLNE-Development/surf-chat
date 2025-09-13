package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.NotificationService
import dev.slne.surf.chat.fallback.entity.NotifySettingsEntity
import dev.slne.surf.chat.fallback.table.NotifySettingsTable
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.util.*

@AutoService(NotificationService::class)
class FallbackNotificationService : NotificationService, Services.Fallback {
    override fun createTable() {
        transaction {
            SchemaUtils.create(NotifySettingsTable)
        }
    }

    override suspend fun pingsEnabled(uuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            NotifySettingsEntity.find { NotifySettingsTable.userUuid eq uuid }
                .firstOrNull()?.pingsEnabled ?: true
        }

    override suspend fun enablePings(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        NotifySettingsTable.upsert(where = { NotifySettingsTable.userUuid eq uuid }) {
            it[pingsEnabled] = true
        }
        return@newSuspendedTransaction
    }

    override suspend fun disablePings(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        NotifySettingsTable.upsert(where = { NotifySettingsTable.userUuid eq uuid }) {
            it[userUuid] = uuid
            it[pingsEnabled] = false
        }

        return@newSuspendedTransaction
    }

    override suspend fun invitesEnabled(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        NotifySettingsEntity.find { NotifySettingsTable.userUuid eq uuid }
            .firstOrNull()?.invitesEnabled ?: true
    }

    override suspend fun enableInvites(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        NotifySettingsTable.upsert(where = { NotifySettingsTable.userUuid eq uuid }) {
            it[userUuid] = uuid
            it[invitesEnabled] = true
        }
        return@newSuspendedTransaction
    }


    override suspend fun disableInvites(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        NotifySettingsTable.upsert(where = { NotifySettingsTable.userUuid eq uuid }) {
            it[userUuid] = uuid
            it[invitesEnabled] = false
        }
        return@newSuspendedTransaction
    }
}