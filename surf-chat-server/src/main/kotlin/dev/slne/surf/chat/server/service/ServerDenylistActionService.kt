package dev.slne.surf.chat.server.service

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.common.service.discordService
import dev.slne.surf.chat.core.common.service.historyService
import dev.slne.surf.chat.server.database.entity.DenylistActionEntity
import dev.slne.surf.chat.server.database.table.DenylistActionsTable
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import dev.slne.surf.cloud.api.common.player.punishment.type.PunishType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.chat.SignedMessage
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.time.Duration.Companion.seconds

@Service
class ServerDenylistActionService {
    suspend fun addAction(action: DenylistAction) =
        newSuspendedTransaction(Dispatchers.IO) {
            DenylistActionEntity.new {
                name = action.name
                actionType = action.actionType
                reason = action.reason
                duration = action.duration
            }
            return@newSuspendedTransaction
        }

    suspend fun removeAction(action: DenylistAction) =
        newSuspendedTransaction(Dispatchers.IO) {
            DenylistActionEntity.find { DenylistActionsTable.name eq action.name }.firstOrNull()
                ?.delete()
            return@newSuspendedTransaction
        }

    suspend fun hasAction(name: String) = newSuspendedTransaction(Dispatchers.IO) {
        val exists = DenylistActionEntity.find { DenylistActionsTable.name eq name }.firstOrNull()
        return@newSuspendedTransaction exists != null
    }

    suspend fun fetchActions() = newSuspendedTransaction(Dispatchers.IO) {
        _actions.clear()
        _actions.addAll(DenylistActionEntity.all().map {
            it.toDto()
        })
        return@newSuspendedTransaction
    }

    fun addLocalAction(action: DenylistAction) = _actions.add(action)
    fun removeLocalAction(action: DenylistAction) = _actions.remove(action)
    fun getLocalAction(name: String) = _actions.firstOrNull { it.name == name }

    fun listLocalActions() = _actions
    fun hasLocalAction(name: String) = _actions.any { it.name == name }
    suspend fun clearActions() = newSuspendedTransaction(Dispatchers.IO) {
        DenylistActionsTable.deleteAll()
    }

    fun clearLocalActions() = _actions.clear()

    suspend fun makeAction(
        messageUuid: UUID,
        entry: DenylistEntry,
        message: SignedMessage,
        sender: OfflineCloudPlayer,
        discordHookUrl: String?
    ) = withContext(Dispatchers.IO) {
        val punishManager = sender.punishmentManager

        when (entry.action.actionType) {
            DenylistActionType.EXPIRABLE_BAN -> {
                punishManager.punish(
                    PunishType.BAN.Expirable(
                        ZonedDateTime.now().plus(
                            entry.action.duration,
                            ChronoUnit.MILLIS
                        )
                    )
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                )
            }

            DenylistActionType.KICK -> {
                punishManager.punish(
                    PunishType.KICK
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                )
            }

            DenylistActionType.PERMANENT_BAN -> {
                punishManager.punish(
                    PunishType.BAN.Permanent
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                )
            }

            DenylistActionType.MUTE -> {
                punishManager.punish(
                    PunishType.MUTE.Expirable(
                        ZonedDateTime.now().plus(
                            entry.action.duration,
                            ChronoUnit.MILLIS
                        )
                    )
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                )
            }

            DenylistActionType.WARN -> {
                punishManager.punish(
                    PunishType.WARN
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                )
            }

            DenylistActionType.COMMUNITY_BAN -> {
                punishManager.punish(
                    PunishType.BAN.Permanent
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                )

                discordHookUrl?.let {
                    discordService.sendCommunityBanNotification(it, sender, entry)
                }
            }
        }

        delay(3.seconds)
        //Bukkit.getServer().deleteMessage(message) TODO: re add
        historyService.markDeleted(messageUuid, "Arty Support (BLOCKED: ${entry.word})")
    }
}