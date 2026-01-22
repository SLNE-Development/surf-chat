package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.FunctionalityService
import dev.slne.surf.chat.fallback.table.FunctionalityTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import net.kyori.adventure.util.Services

@AutoService(FunctionalityService::class)
class FunctionalityServiceImpl : FunctionalityService, Services.Fallback {
    var localChatEnabled = true

    override suspend fun isEnabledForServer(server: String): Boolean = suspendTransaction {
        FunctionalityTable.selectAll().where(FunctionalityTable.server eq server).firstOrNull()
            ?.let {
                return@suspendTransaction it[FunctionalityTable.chatEnabled]
            } ?: true
    }

    override suspend fun getAllServers(): ObjectSet<String> = suspendTransaction {
        FunctionalityTable.selectAll().map {
            it[FunctionalityTable.server]
        }.toSet().toObjectSet()
    }

    override suspend fun getAllEnabledServers(): ObjectSet<String> = suspendTransaction {
        FunctionalityTable.selectAll().where(FunctionalityTable.chatEnabled eq true).map {
            it[FunctionalityTable.server]
        }.toSet().toObjectSet()
    }

    override suspend fun getAllDisabledServers(): ObjectSet<String> = suspendTransaction {
        FunctionalityTable.selectAll().where(FunctionalityTable.chatEnabled eq false).map {
            it[FunctionalityTable.server]
        }.toSet().toObjectSet()
    }

    override suspend fun fetch(localServer: String) = suspendTransaction {
        localChatEnabled = FunctionalityTable.selectAll()
            .where(FunctionalityTable.server eq localServer).firstOrNull()?.let {
                it[FunctionalityTable.chatEnabled]
            } ?: true
    }

    override suspend fun enableLocalChat(localServer: String) {
        localChatEnabled = true

        suspendTransaction {
            FunctionalityTable.upsert {
                it[FunctionalityTable.server] = localServer
                it[chatEnabled] = localChatEnabled
            }
        }
    }

    override suspend fun toggleLocalChat(localServer: String): Boolean {
        localChatEnabled = !localChatEnabled

        suspendTransaction {
            FunctionalityTable.upsert {
                it[FunctionalityTable.server] = localServer
                it[chatEnabled] = localChatEnabled
            }
        }

        return localChatEnabled
    }

    override suspend fun disableLocalChat(localServer: String) {
        localChatEnabled = false

        suspendTransaction {
            FunctionalityTable.upsert {
                it[FunctionalityTable.server] = localServer
                it[chatEnabled] = false
            }
        }
    }

    override fun isLocalChatEnabled() = localChatEnabled
}