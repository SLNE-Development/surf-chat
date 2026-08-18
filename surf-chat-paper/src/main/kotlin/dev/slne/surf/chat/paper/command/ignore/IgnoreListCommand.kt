package dev.slne.surf.chat.paper.command.ignore

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.api.core.util.mapAsync
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.core.client.util.unixTime
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.core.api.common.SurfCoreApi
import net.kyori.adventure.text.format.TextDecoration
import java.time.OffsetDateTime

private val pagination = Pagination<IgnoreListPaginationEntry> {
    title {
        primary("Ignorierte Spieler".toSmallCaps(), TextDecoration.BOLD)
    }

    rowRenderer { entry, _ ->
        listOf(
            buildText {
                append(CommonComponents.EM_DASH)
                appendSpace()
                variableKey(entry.targetName)
                appendSpace()
                spacer("(${entry.createdAt.unixTime()})")
            }
        )
    }
}

fun CommandAPICommand.ignoreListCommand() = subcommand("#list") {
    withPermission(PermissionRegistry.COMMAND_IGNORE_LIST)
    playerExecutorSuspend { player, _ ->
        val ignoreList = IgnoreService.getCachedIgnoreList(player.uniqueId)
            .mapAsync {
                IgnoreListPaginationEntry(
                    targetName = SurfCoreApi.getOfflinePlayer(it.target)?.lastKnownName ?: it.target.toString(),
                    targetUuid = it.target.toString(),
                    createdAt = it.createdAt
                )
            }

        if (ignoreList.isEmpty()) {
            throw CommandAPI.failWithString("Du ignorierst aktuell niemanden.")
        }

        player.sendText {
            append(pagination.renderComponent(ignoreList))
        }
    }
}

private data class IgnoreListPaginationEntry(
    val targetName: String,
    val targetUuid: String,
    val createdAt: OffsetDateTime
)