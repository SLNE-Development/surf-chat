package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.server.ChatServer
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

    override suspend fun isEnabledForServer(server: ChatServer): Boolean = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityEntity.find { FunctionalityTable.server eq server.internalName }
            .firstOrNull()?.chatEnabled ?: true
    }

    override suspend fun getAllServers(): ObjectSet<ChatServer> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityEntity.all().map {
            ChatServer.of(it.server)
        }.toObjectSet()
    }

    override suspend fun getAllEnabledServers(): ObjectSet<ChatServer> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityEntity.find { FunctionalityTable.chatEnabled eq true }.map {
            ChatServer.of(it.server)
        }.toObjectSet()
    }

    override suspend fun getAllDisabledServers(): ObjectSet<ChatServer> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityEntity.find { FunctionalityTable.chatEnabled eq false }.map {
            ChatServer.of(it.server)
        }.toObjectSet()
    }

    override suspend fun fetch(localServer: ChatServer) = newSuspendedTransaction(Dispatchers.IO) {
        localChatEnabled =
            FunctionalityEntity.find { FunctionalityTable.server eq localServer.internalName }
                .firstOrNull()?.chatEnabled ?: true
    }

    override suspend fun enableLocalChat(localServer: ChatServer) {
        localChatEnabled = true

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert(where = { FunctionalityTable.server eq localServer.internalName }) {
                it[chatEnabled] = true
            }
        }
    }

    override suspend fun toggleLocalChat(localServer: ChatServer): Boolean {
        localChatEnabled = !localChatEnabled

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert(where = { FunctionalityTable.server eq localServer.internalName }) {
                it[chatEnabled] = localChatEnabled
            }
        }

        return localChatEnabled
    }

    override suspend fun disableLocalChat(localServer: ChatServer) {
        localChatEnabled = false

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert(where = { FunctionalityTable.server eq localServer.internalName }) {
                it[chatEnabled] = false
            }
        }
    }

    override fun isLocalChatEnabled() = localChatEnabled
}