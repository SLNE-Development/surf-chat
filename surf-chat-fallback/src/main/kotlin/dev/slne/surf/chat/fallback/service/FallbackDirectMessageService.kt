package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.DirectMessageService
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

@AutoService(DirectMessageService::class)
class FallbackDirectMessageService : DirectMessageService, Services.Fallback {
    object DirectMessageSettings : Table("chat_settings_direct_messages") {
        val userUuid =
            varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val directMessagesEnabled = bool("direct_messages_enabled").default(true)

        override val primaryKey = PrimaryKey(userUuid)
    }

    override fun createTable() {
        transaction {
            SchemaUtils.create(DirectMessageSettings)
        }
    }

    override suspend fun directMessagesEnabled(uuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            DirectMessageSettings.selectAll().where(DirectMessageSettings.userUuid eq uuid)
                .firstOrNull()?.let {
                    it[DirectMessageSettings.directMessagesEnabled]
                } ?: true
        }

    override suspend fun enableDirectMessages(uuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            DirectMessageSettings.upsert {
                it[userUuid] = uuid
                it[directMessagesEnabled] = true
            }
            return@newSuspendedTransaction
        }

    override suspend fun disableDirectMessages(uuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            DirectMessageSettings.upsert {
                it[userUuid] = uuid
                it[directMessagesEnabled] = false
            }
            return@newSuspendedTransaction
        }
}