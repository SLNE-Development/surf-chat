package dev.slne.surf.chat.minestom.command.surfchat

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPI
import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.greedyStringArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.core.client.lookup.RenderData
import dev.slne.surf.chat.core.client.lookup.lookupPagination
import dev.slne.surf.chat.core.client.lookup.parseFilters
import dev.slne.surf.chat.core.client.lookup.resolveSenderNames
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.common.service.HistoryService
import kotlinx.coroutines.TimeoutCancellationException

fun CommandAPICommand.surfChatLookupCommand(): CommandAPICommand = withSubcommand(
    subcommand("lookup") {
        withPermission(ChatPermissions.COMMAND_SURFCHAT_LOOKUP)
        greedyStringArgument("query", optional = true)

        playerExecutorSuspend { player, args ->
            val query: String? by args

            player.sendText {
                appendInfoPrefix()
                info("Es wird nach Ergebnissen gesucht...")
            }

            val queryMap = query?.parseQueryString()
            val filter = queryMap?.parseFilters() ?: HistoryFilter.empty()
            val page = queryMap?.get("--page")?.toIntOrNull() ?: 1
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
)

private val queryPairRegex = Regex("""\s+(?=--)""")

private fun String.parseQueryString(): Map<String, String> = trim()
    .split(queryPairRegex)
    .filter { it.startsWith("--") }
    .associate { pair -> pair.substringBefore(' ') to pair.substringAfter(' ', "") }
