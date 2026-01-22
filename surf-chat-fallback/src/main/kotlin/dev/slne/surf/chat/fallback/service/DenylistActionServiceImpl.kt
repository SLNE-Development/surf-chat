package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.DenylistActionService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.fallback.table.DenylistActionsTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import java.util.*
import kotlin.time.Duration.Companion.seconds

@AutoService(DenylistActionService::class)
class DenylistActionServiceImpl : DenylistActionService, Services.Fallback {
    private val _actions = mutableObjectSetOf<DenylistAction>()

    override suspend fun addAction(action: DenylistAction) =
        suspendTransaction {
            DenylistActionsTable.insert {
                it[name] = action.name
                it[actionType] = action.actionType
                it[reason] = action.reason
                it[duration] = action.duration
            }
            return@suspendTransaction
        }

    override suspend fun removeAction(action: DenylistAction) =
        suspendTransaction {
            DenylistActionsTable.deleteWhere {
                DenylistActionsTable.name eq action.name
            }
            return@suspendTransaction
        }

    override suspend fun hasAction(name: String) = suspendTransaction {
        return@suspendTransaction DenylistActionsTable.selectAll()
            .where(DenylistActionsTable.name eq name).count() > 0
    }

    override suspend fun getActionById(id: Long): DenylistAction? = suspendTransaction {
        DenylistActionsTable.selectAll()
            .where(DenylistActionsTable.id eq id).map {
                DenylistAction(
                    name = it[DenylistActionsTable.name],
                    actionType = it[DenylistActionsTable.actionType],
                    reason = it[DenylistActionsTable.reason],
                    duration = it[DenylistActionsTable.duration]
                )
            }.firstOrNull()
    }

    override suspend fun fetchActions() = suspendTransaction {
        _actions.clear()
        _actions.addAll(DenylistActionsTable.selectAll().map {
            DenylistAction(
                name = it[DenylistActionsTable.name],
                actionType = it[DenylistActionsTable.actionType],
                reason = it[DenylistActionsTable.reason],
                duration = it[DenylistActionsTable.duration]
            )
        }.toList())
        return@suspendTransaction
    }

    override fun addLocalAction(action: DenylistAction) = _actions.add(action)
    override fun removeLocalAction(action: DenylistAction) = _actions.remove(action)
    override fun getLocalAction(name: String) = _actions.firstOrNull { it.name == name }

    override fun listLocalActions() = _actions
    override fun hasLocalAction(name: String) = _actions.any { it.name == name }
    override suspend fun clearActions() = suspendTransaction {
        DenylistActionsTable.deleteAll()
    }

    override fun clearLocalActions() = _actions.clear()

    override suspend fun makeAction(
        messageUuid: UUID,
        entry: DenylistEntry,
        message: SignedMessage,
        sender: User,
        discordHookUrl: String?
    ) = withContext(Dispatchers.IO) {
        delay(3.seconds)
        Bukkit.getServer().deleteMessage(message)
        historyService.markDeleted(messageUuid, "Arty Support (BLOCKED: ${entry.word})")
    }
}