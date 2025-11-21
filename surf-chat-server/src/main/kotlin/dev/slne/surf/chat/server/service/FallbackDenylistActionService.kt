package dev.slne.surf.chat.server.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.service.DenylistActionService
import dev.slne.surf.chat.core.service.discordService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.server.entity.DenylistActionEntity
import dev.slne.surf.chat.server.table.DenylistActionsTable
import dev.slne.surf.chat.server.util.toOfflineCloudPlayer
import dev.slne.surf.cloud.api.common.player.punishment.type.PunishType
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
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
        if (isCloud()) {

            val cloudPlayer = sender.toOfflineCloudPlayer()
            val punishManager = cloudPlayer.punishmentManager

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
        } else {
            logger().atWarning()
                .log("Unable to establish Cloud connection. Punishment actions for ${sender.name}: ${entry.word} / ${entry.action.actionType} will be ignored.")
        }

        delay(3.seconds)
        Bukkit.getServer().deleteMessage(message)
        historyService.markDeleted(messageUuid, "Arty Support (BLOCKED: ${entry.word})")
    }

    private fun isCloud() = Bukkit.getPluginManager().isPluginEnabled("surf-cloud-bukkit")
}