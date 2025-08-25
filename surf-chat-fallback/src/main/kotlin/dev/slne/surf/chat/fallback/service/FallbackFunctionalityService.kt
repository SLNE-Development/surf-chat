package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.core.service.FunctionalityService
import dev.slne.surf.chat.fallback.table.FunctionalityTable
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
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
        FunctionalityTable.selectAll().where(FunctionalityTable.server eq server.internalName)
            .firstOrNull()?.let {
                it[FunctionalityTable.chatEnabled]
            } ?: true
    }

    override suspend fun getAllServers(): ObjectSet<ChatServer> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityTable.selectAll().map {
            ChatServer.of(it[FunctionalityTable.server])
        }.toObjectSet()
    }

    override suspend fun getAllEnabledServers(): ObjectSet<ChatServer> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityTable.selectAll().where(FunctionalityTable.chatEnabled eq true).map {
            ChatServer.of(it[FunctionalityTable.server])
        }.toObjectSet()
    }

    override suspend fun getAllDisabledServers(): ObjectSet<ChatServer> = newSuspendedTransaction(
        Dispatchers.IO
    ) {
        FunctionalityTable.selectAll().where(FunctionalityTable.chatEnabled eq false).map {
            ChatServer.of(it[FunctionalityTable.server])
        }.toObjectSet()
    }

    override suspend fun fetch(localServer: ChatServer) = newSuspendedTransaction(Dispatchers.IO) {
        val result = FunctionalityTable.selectAll()
            .where(FunctionalityTable.server eq localServer.internalName)
            .firstOrNull()
        localChatEnabled = result?.get(FunctionalityTable.chatEnabled) ?: true
    }

    override suspend fun enableLocalChat(localServer: ChatServer) {
        localChatEnabled = true

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert {
                it[server] = localServer.internalName
                it[chatEnabled] = true
            }
        }
    }

    override suspend fun toggleLocalChat(localServer: ChatServer): Boolean {
        localChatEnabled = !localChatEnabled

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert {
                it[server] = localServer.internalName
                it[chatEnabled] = localChatEnabled
            }
        }

        return localChatEnabled
    }

    override suspend fun disableLocalChat(localServer: ChatServer) {
        localChatEnabled = false

        newSuspendedTransaction(Dispatchers.IO) {
            FunctionalityTable.upsert {
                it[server] = localServer.internalName
                it[chatEnabled] = false
            }
        }
    }

    override fun isLocalChatEnabled(): Boolean {
        return localChatEnabled
    }
}