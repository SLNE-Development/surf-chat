package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.DenylistActionService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.fallback.entity.DenylistActionEntity
import dev.slne.surf.chat.fallback.table.DenylistActionsTable
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import kotlin.time.Duration.Companion.seconds

@Suppress("MISSING_DEPENDENCY_SUPERCLASS_IN_TYPE_ARGUMENT")
@AutoService(DenylistActionService::class)
class FallbackDenylistActionService : DenylistActionService, Services.Fallback {
    private val _actions = mutableObjectSetOf<DenylistAction>()

    override suspend fun addAction(action: DenylistAction) =
        newSuspendedTransaction(Dispatchers.IO) {
            DenylistActionEntity.new {
                name = action.name
                actionType = action.actionType
                reason = action.reason
                duration = action.duration
            }
            return@newSuspendedTransaction
        }

    override suspend fun removeAction(action: DenylistAction) =
        newSuspendedTransaction(Dispatchers.IO) {
            DenylistActionEntity.find { DenylistActionsTable.name eq action.name }.firstOrNull()
                ?.delete()
            return@newSuspendedTransaction
        }

    override suspend fun hasAction(name: String) = newSuspendedTransaction(Dispatchers.IO) {
        val exists = DenylistActionEntity.find { DenylistActionsTable.name eq name }.firstOrNull()
        return@newSuspendedTransaction exists != null
    }

    override suspend fun fetchActions() = newSuspendedTransaction(Dispatchers.IO) {
        _actions.clear()
        _actions.addAll(DenylistActionEntity.all().map {
            it.toDto()
        })
        return@newSuspendedTransaction
    }

    override fun addLocalAction(action: DenylistAction) = _actions.add(action)
    override fun removeLocalAction(action: DenylistAction) = _actions.remove(action)
    override fun getLocalAction(name: String) = _actions.firstOrNull { it.name == name }

    override fun listLocalActions() = _actions
    override fun hasLocalAction(name: String) = _actions.any { it.name == name }
    override suspend fun clearActions() = newSuspendedTransaction(Dispatchers.IO) {
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