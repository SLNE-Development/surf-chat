package dev.slne.surf.chat.bukkit.command.ignore

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.util.unixTime
import dev.slne.surf.chat.core.service.ignoreService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

private val pagination = Pagination<IgnoreListEntry> {
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
    playerExecutor { player, _ ->
        val ignoreList = ignoreService.getCachedIgnoreList(player.uniqueId)

        if (ignoreList.isEmpty()) {
            throw CommandAPI.failWithString("Du ignorierst aktuell niemanden.")
        }

        player.sendText {
            append(pagination.renderComponent(ignoreList))
        }
    }
}