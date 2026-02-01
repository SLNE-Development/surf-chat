package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.MapArgumentBuilder
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.optionalArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.util.appendLinePrefix
import dev.slne.surf.chat.bukkit.util.formatAgo
import dev.slne.surf.chat.bukkit.util.formatTime
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.format.TextDecoration
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private val pagination = Pagination<RenderData> {
    title {
        info("Suchergebnisse".toSmallCaps(), TextDecoration.BOLD)
    }
    resultsPerPage = 10

    rowRenderer { (senderNames, entry), _ ->
        listOf(
            buildText {
                appendLinePrefix()
                append {
                    appendSpace()
                    spacer(entry.sentAt.formatAgo())
                    if (entry.deletedBy != null) {
                        decorate(TextDecoration.STRIKETHROUGH)
                    }

                    hoverEvent(buildText {
                        spacer(entry.sentAt.formatTime())
                    })
                }
                appendSpace()
                if (entry.deleted) {
                    append {
                        error("✘")
                        hoverEvent(buildText {
                            error("Gelöscht von ")
                            error(entry.deletedBy?.toString() ?: "Unbekannt")
                            appendNewline()
                            error("Gelöscht am ")
                            error(entry.deletedAt!!.formatTime())
                        })
                    }
                } else {
                    spacer("-")
                }
                appendSpace()
                append {
                    val name = senderNames[entry.senderUuid] ?: "#Unbekannt"
                    variableValue(name)
                    if (entry.deletedBy != null) {
                        decorate(TextDecoration.STRIKETHROUGH)
                    }
                    hoverEvent(buildText {
                        spacer("Klicke, um das Profil zu öffnen.")
                    })
                    clickOpensUrl("https://laby.net/$name")
                }
                appendSpace()
                append {
                    info("schrieb")
                    if (entry.deletedBy != null) {
                        decorate(TextDecoration.STRIKETHROUGH)
                    }
                    hoverEvent(buildText {
                        info("auf Server ")
                        variableValue(entry.server)
                    })
                }
                appendSpace()
                append {
                    text(entry.message.take(20), Colors.WHITE)
                    if (entry.message.length > 20) {
                        text("...", Colors.GRAY)
                    }
                    if (entry.deletedBy != null) {
                        decorate(TextDecoration.STRIKETHROUGH)
                    }

                    hoverEvent(buildText {
                        text(entry.message, Colors.WHITE)
                    })
                }
                appendSpace()
                spacer("(")
                variableValue(entry.messageType.name)
                spacer(")")
            }
        )
    }
}

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
            historyService.findHistoryEntry(filter)
        } catch (e: TimeoutCancellationException) {
            throw CommandAPI.failWithString("Die Suche hat zu lange gedauert und wurde abgebrochen.")
        }

        if (history.isEmpty()) {
            throw CommandAPI.failWithString("Es wurden keine Ergebnisse gefunden.")
        }

        val senderNames = ConcurrentHashMap<UUID, String>()
        coroutineScope {
            for (entry in history) {
                if (senderNames.containsKey(entry.senderUuid)) continue
                launch {
                    val sender = entry.sender()
                    val senderName = sender.lastKnownName ?: entry.senderUuid.toString()
                    senderNames[entry.senderUuid] = senderName
                }
            }
        }

        val renderData = history.map { entry ->
            RenderData(
                senderNames = senderNames,
                entry = entry
            )
        }

        player.sendText {
            append(pagination.renderComponent(renderData, page))
        }
    }
}

private val regex = Regex("""(\d+)([smhdw])""", RegexOption.IGNORE_CASE)

private suspend fun Map<String, String>.parseFilters(): HistoryFilter {
    val senderUuid = this["--sender"]?.let { PlayerLookupService.getUuid(it) }
    val receiverUuid = this["--receiver"]?.let { PlayerLookupService.getUuid(it) }
    val deleted = this["--deleted"]?.toBoolean()
    val deletedBy = this["--deletedBy"]?.let { PlayerLookupService.getUuid(it) }

    fun parseRangeToMillis(input: String): Long? {
        val match = regex.matchEntire(input.trim()) ?: return null

        val (valueStr, unit) = match.destructured
        val value = valueStr.toLongOrNull() ?: return null

        val millis = when (unit.lowercase()) {
            "s" -> value * 1000
            "m" -> value * 60 * 1000
            "h" -> value * 60 * 60 * 1000
            "d" -> value * 24 * 60 * 60 * 1000
            "w" -> value * 7 * 24 * 60 * 60 * 1000
            else -> return null
        }

        return millis
    }

    return HistoryFilter(
        messageUuid = this["--messageUuid"]?.let { runCatching { UUID.fromString(it) }.getOrNull() },
        senderUuid = senderUuid,
        receiverUuid = receiverUuid,
        messageType = this["--type"]?.let { runCatching { MessageType.valueOf(it.uppercase()) }.getOrNull() },
        after = this["--range"]?.let {
            parseRangeToMillis(it)?.let { amountToSubtract ->
                OffsetDateTime.now().minus(amountToSubtract, java.time.temporal.ChronoUnit.MILLIS)
            }
        },
        messageLike = this["--message"],
        server = this["--server"],
        deletedBy = deletedBy,
        deleted = deleted,
        limit = this["--limit"]?.toIntOrNull() ?: 50,
    )
}


private data class RenderData(
    val senderNames: Map<UUID, String>,
    val entry: HistoryEntry
)
