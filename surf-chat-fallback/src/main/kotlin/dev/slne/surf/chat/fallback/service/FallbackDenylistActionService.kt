package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.DenylistActionType
import dev.slne.surf.chat.core.service.DenylistActionService
import dev.slne.surf.chat.fallback.entity.DenylistActionEntity
import dev.slne.surf.chat.fallback.table.DenylistActionsTable
import dev.slne.surf.chat.fallback.util.appendBotIcon
import dev.slne.surf.chat.fallback.util.sendTeamMessage
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.time.Duration.Companion.seconds

@AutoService(DenylistActionService::class)
class FallbackDenylistActionService : DenylistActionService, Services.Fallback {
    val localActions = mutableObjectSetOf<DenylistAction>()

    override suspend fun addAction(action: DenylistAction) =
        newSuspendedTransaction(Dispatchers.IO) {
            DenylistActionEntity.new {
                name = action.name
                actionType = action.actionType
                reason = action.reason
            }
            return@newSuspendedTransaction
        }

    override suspend fun removeAction(action: DenylistAction) =
        newSuspendedTransaction(Dispatchers.IO) {
            DenylistActionEntity.find { DenylistActionsTable.name eq action.name }.firstOrNull()
                ?.delete()
            return@newSuspendedTransaction
        }

    override suspend fun fetchActions() = newSuspendedTransaction(Dispatchers.IO) {
        localActions.clear()
        localActions.addAll(DenylistActionEntity.all().map {
            it.toDto()
        })
        return@newSuspendedTransaction
    }

    override fun addLocalAction(action: DenylistAction) = localActions.add(action)
    override fun removeLocalAction(action: DenylistAction) = localActions.remove(action)
    override fun getLocalAction(name: String) = localActions.firstOrNull { it.name == name }

    override fun listLocalActions() = localActions
    override suspend fun makeAction(
        action: DenylistAction,
        message: SignedMessage,
        sender: User
    ) {
        when (action.actionType) {
            DenylistActionType.NOTIFY -> {
                sendTeamMessage {
                    appendBotIcon()
                    info("Eine Nachricht von ")
                    variableValue(sender.name)
                    info(" wurde blockiert.")

                    hoverEvent(buildText {
                        info(message.message())
                    })
                }
            }

            DenylistActionType.BAN -> TODO()
            DenylistActionType.KICK -> TODO()
            DenylistActionType.MUTE -> TODO()
            DenylistActionType.WARN -> TODO()
        }

        delay(3.seconds)
        Bukkit.getServer().deleteMessage(message)
    }
}