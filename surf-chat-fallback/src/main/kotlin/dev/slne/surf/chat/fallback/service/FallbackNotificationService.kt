package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.NotificationService
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.util.*

@AutoService(NotificationService::class)
class FallbackNotificationService : NotificationService, Services.Fallback {
    object NotificationSettings : Table("chat_settings_notifications") {
        val userUuid =
            varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val pingsEnabled = bool("pings_enabled").default(true)

        override val primaryKey = PrimaryKey(userUuid)
    }

    override fun createTable() {
        transaction {
            SchemaUtils.create(NotificationSettings)
        }
    }

    override suspend fun pingsEnabled(uuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            NotificationSettings.selectAll().where(NotificationSettings.userUuid eq uuid)
                .firstOrNull()?.let {
                    it[NotificationSettings.pingsEnabled]
                } ?: true
        }

    override suspend fun enablePings(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        NotificationSettings.upsert {
            it[userUuid] = uuid
            it[pingsEnabled] = true
        }
        return@newSuspendedTransaction
    }

    override suspend fun disablePings(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        NotificationSettings.upsert {
            it[userUuid] = uuid
            it[pingsEnabled] = false
        }

        return@newSuspendedTransaction
    }
}