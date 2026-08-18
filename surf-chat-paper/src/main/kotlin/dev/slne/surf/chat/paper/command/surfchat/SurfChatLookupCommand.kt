package dev.slne.surf.chat.paper.command.surfchat

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.MapArgumentBuilder
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.optionalArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.core.client.lookup.RenderData
import dev.slne.surf.chat.core.client.lookup.lookupPagination
import dev.slne.surf.chat.core.client.lookup.parseFilters
import dev.slne.surf.chat.core.client.lookup.resolveSenderNames
import dev.slne.surf.chat.core.common.service.HistoryService
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import kotlinx.coroutines.TimeoutCancellationException

fun CommandAPICommand.surfChatLookupCommand() = subcommand("lookup") {
    withPermission(PermissionRegistry.COMMAND_SURFCHAT_LOOKUP)
    optionalArgument(
        MapArgumentBuilder<String, String>("query", ' ')
            .withKeyMapper { it }
            .withValueMapper { it }
            .withKeyList(
                listOf(
                    "--type",
                    "--range",
                    "--message",
                    "--deletedBy",
                    "--deleted",
                    "--page",
                    "--limit",
                    "--server",
                    "--sender",
                    "--receiver",
                    "--messageUuid"
                )
            )
            .withoutValueList()
            .build()
    )

    playerExecutorSuspend { player, args ->
        val query: Map<String, String>? by args

        player.sendText {
            appendInfoPrefix()
            info("Es wird nach Ergebnissen gesucht...")
        }

        val filter = query?.parseFilters() ?: HistoryFilter.empty()
        val page = query?.get("--page")?.toIntOrNull() ?: 1
        val history = try {
            HistoryService.findHistoryEntry(filter)
        } catch (e: TimeoutCancellationException) {
            throw CommandAPI.failWithString("Die Suche hat zu lange gedauert und wurde abgebrochen.")
        }

        if (history.isEmpty()) {
            throw CommandAPI.failWithString("Es wurden keine Ergebnisse gefunden.")
        }

        val senderNames = resolveSenderNames(history)
        val renderData = history.map { entry ->
            RenderData(
                senderNames = senderNames,
                entry = entry
            )
        }

        player.sendText {
            append(lookupPagination.renderComponent(renderData, page))
        }
    }
}
