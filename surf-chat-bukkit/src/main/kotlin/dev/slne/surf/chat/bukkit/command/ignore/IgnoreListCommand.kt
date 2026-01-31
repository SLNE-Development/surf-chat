package dev.slne.surf.chat.bukkit.command.ignore

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.util.unixTime
import dev.slne.surf.chat.core.service.ignoreService
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import dev.slne.surf.surfapi.core.api.util.mapAsync
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

fun CommandAPICommand.ignoreListCommand() = subcommand("list") {
    withPermission(PermissionRegistry.COMMAND_IGNORE_LIST)
    playerExecutorSuspend { player, _ ->
        val ignoreList = ignoreService.getCachedIgnoreList(player.uniqueId)
            .mapAsync {
                IgnoreListPaginationEntry(
                    targetName = surfCoreApi.getOfflinePlayer(it.target)?.lastKnownName ?: it.target.toString(),
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