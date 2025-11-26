package dev.slne.surf.chat.server.database.repository

import dev.slne.surf.chat.api.ChatUuid
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.common.netty.packet.clientbound.ClientboundMessageDeletePacket
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.server.config.discordConfig
import dev.slne.surf.chat.server.database.entity.DenylistActionEntity
import dev.slne.surf.chat.server.service.DiscordService
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import dev.slne.surf.cloud.api.common.player.punishment.type.PunishType
import dev.slne.surf.cloud.api.server.netty.packet.broadcast
import dev.slne.surf.cloud.api.server.plugin.CoroutineTransactional
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.chat.SignedMessage
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

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
        messageUuid: ChatUuid,
        entry: DenylistEntry,
        signature: SignedMessage.Signature?,
        sender: OfflineCloudPlayer
    ) = withContext(Dispatchers.IO) {
        signature?.let {
            ClientboundMessageDeletePacket(it).broadcast()
        }

        val punishManager = sender.punishmentManager

        var punishmentId: String

        when (entry.action.actionType) {
            DenylistActionType.EXPIRABLE_BAN -> {
                punishmentId = punishManager.punish(
                    PunishType.BAN.Expirable(
                        ZonedDateTime.now().plus(
                            entry.action.duration,
                            ChronoUnit.MILLIS
                        )
                    )
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                ).punishmentId
            }

            DenylistActionType.KICK -> {
                punishmentId = punishManager.punish(
                    PunishType.KICK
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                ).punishmentId
            }

            DenylistActionType.PERMANENT_BAN -> {
                punishmentId = punishManager.punish(
                    PunishType.BAN.Permanent
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                ).punishmentId
            }

            DenylistActionType.MUTE -> {
                punishmentId = punishManager.punish(
                    PunishType.MUTE.Expirable(
                        ZonedDateTime.now().plus(
                            entry.action.duration,
                            ChronoUnit.MILLIS
                        )
                    )
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                ).punishmentId
            }

            DenylistActionType.WARN -> {
                punishmentId = punishManager.punish(
                    PunishType.WARN
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                ).punishmentId
            }

            DenylistActionType.COMMUNITY_BAN -> {
                punishmentId = punishManager.punish(
                    PunishType.BAN.Permanent
                        .withNote("Punished by Arty Support (surf-chat) for: ${entry.word} (messageUid: $messageUuid)"),
                    entry.action.reason
                ).punishmentId

                discordService.sendCommunityBanNotification(
                    discordConfig.config.webhook,
                    sender,
                    entry,
                    punishmentId
                )
            }
        }

        historyRepository.markDeleted(messageUuid, "Arty Support (BLOCKED: ${entry.word})")
    }
}