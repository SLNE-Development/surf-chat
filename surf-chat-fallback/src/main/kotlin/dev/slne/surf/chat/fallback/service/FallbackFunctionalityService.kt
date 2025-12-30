package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.FunctionalityService
import dev.slne.surf.chat.fallback.entity.FunctionalityEntity
import dev.slne.surf.chat.fallback.table.FunctionalityTable
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

@AutoService(FunctionalityService::class)
class FallbackFunctionalityService : FunctionalityService, Services.Fallback {
    var localChatEnabled = true
    override fun createTable() {
        transaction {
            SchemaUtils.create(FunctionalityTable)
        }
    }

    override suspend fun isEnabledForServer(server: String): Boolean = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityEntity.find { FunctionalityTable.server eq server }
            .firstOrNull()?.chatEnabled ?: true
    }

    override suspend fun getAllServers(): ObjectSet<String> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityEntity.all().map {
            it.server
        }.toObjectSet()
    }

    override suspend fun getAllEnabledServers(): ObjectSet<String> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityEntity.find { FunctionalityTable.chatEnabled eq true }.map {
            it.server
        }.toObjectSet()
    }

    override suspend fun getAllDisabledServers(): ObjectSet<String> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityEntity.find { FunctionalityTable.chatEnabled eq false }.map {
            it.server
        }.toObjectSet()
    }

    override suspend fun fetch(localServer: String) = newSuspendedTransaction(Dispatchers.IO) {
        localChatEnabled =
            FunctionalityEntity.find { FunctionalityTable.server eq localServer }
                .firstOrNull()?.chatEnabled ?: true
    }

    override suspend fun enableLocalChat(localServer: String) {
        localChatEnabled = true

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert(where = { FunctionalityTable.server eq localServer }) {
                it[chatEnabled] = true
            }
        }
    }

    override suspend fun toggleLocalChat(localServer: String): Boolean {
        localChatEnabled = !localChatEnabled

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert(where = { FunctionalityTable.server eq localServer }) {
                it[chatEnabled] = localChatEnabled
            }
        }

        return localChatEnabled
    }

    override suspend fun disableLocalChat(localServer: String) {
        localChatEnabled = false

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert(where = { FunctionalityTable.server eq localServer }) {
                it[chatEnabled] = false
            }
        }
    }

    override fun isLocalChatEnabled() = localChatEnabled
}