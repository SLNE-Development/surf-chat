package dev.slne.surf.chat.server.database.repository

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.server.database.entity.DenylistActionEntity
import dev.slne.surf.chat.server.service.DiscordService
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import dev.slne.surf.cloud.api.common.player.punishment.type.PunishType
import dev.slne.surf.cloud.api.common.util.mutableObjectSetOf
import dev.slne.surf.cloud.api.server.plugin.CoroutineTransactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.chat.SignedMessage
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.time.Duration.Companion.seconds

@Service
@CoroutineTransactional
class DenylistActionRepository(
    private val discordService: DiscordService,
    private val historyRepository: HistoryRepository
) {
    suspend fun cacheActions() {
        val actions = mutableObjectSetOf<DenylistAction>()

        DenylistActionEntity.all().forEach {
            actions.add(
                DenylistAction(
                    it.name,
                    it.actionType,
                    it.reason,
                    it.duration
                )
            )
        }

        SyncValues.denylistActions.clear()
        SyncValues.denylistActions.addAll(actions)
    }

    suspend fun storeActions() {
        DenylistActionEntity.all().forEach { it.delete() }

        SyncValues.denylistActions.forEach {
            DenylistActionEntity.new {
                name = it.name
                actionType = it.actionType
                reason = it.reason
                duration = it.duration
            }
        }
    }

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

        historyRepository.markDeleted(messageUuid, "Arty Support (BLOCKED: ${entry.word})")
    }
}