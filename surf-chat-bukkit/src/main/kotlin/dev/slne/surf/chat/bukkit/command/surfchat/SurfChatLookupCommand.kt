package dev.slne.surf.chat.bukkit.command.surfchat

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.MapArgumentBuilder
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.optionalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.appendLinePrefix
import dev.slne.surf.chat.bukkit.util.formatAgo
import dev.slne.surf.chat.bukkit.util.formatTime
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

fun CommandAPICommand.surfChatLookupCommand() = subcommand("lookup") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_LOOKUP)
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
                    "--page",
                    "--limit",
                    "--server",
                    "--sender",
                    "--receiver",
                    "--channel",
                    "--messageUuid"
                )
            )
            .withoutValueList()
            .build()
    )

    playerExecutor { player, args ->
        val query: Map<String, String>? by args

        player.sendText {
            appendPrefix()
            info("Es wird nach Ergebnissen gesucht...")
        }

        plugin.launch {
            val filter = query?.parseFilters() ?: HistoryFilter.empty()
            val page = query?.get("--page")?.toIntOrNull() ?: 1

            val history = historyService.findHistoryEntry(filter).sortedByDescending { it.sentAt }

            if (history.isEmpty()) {
                player.sendText {
                    appendPrefix()
                    error("Es wurden keine Ergebnisse gefunden.")
                }
                return@launch
            }

            val senderNames = history.map { it.senderUuid }
                .distinct()
                .associateWith { PlayerLookupService.getUsername(it) ?: "Unbekannt" }

            val pagination = Pagination<HistoryEntry> {
                title {
                    info("Suchergebnisse".toSmallCaps(), TextDecoration.BOLD)
                }
                resultsPerPage = 10

                rowRenderer { entry, _ ->
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
                            if (entry.deletedBy != null) {
                                append {
                                    error("✘")
                                    hoverEvent(buildText {
                                        error("Gelöscht von ")
                                        error(entry.deletedBy ?: "Unbekannt")
                                    })
                                }
                            } else {
                                spacer("-")
                            }
                            appendSpace()
                            append {
                                val name = senderNames[entry.senderUuid] ?: "Unbekannt"
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

                                    val channel = entry.channel

                                    if (channel != null) {
                                        appendSpace()
                                        info("im Kanal ")
                                        variableValue(channel)
                                    }
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

            player.sendText {
                append(pagination.renderComponent(history, page))
            }
        }
    }
}

private val regex = Regex("""(\d+)([smhdw])""", RegexOption.IGNORE_CASE)

private suspend fun Map<String, String>.parseFilters(): HistoryFilter {
    val senderUuid = this["--sender"]?.let { PlayerLookupService.getUuid(it) }
    val receiverUuid = this["--receiver"]?.let { PlayerLookupService.getUuid(it) }

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
        range = this["--range"]?.let { parseRangeToMillis(it) },
        messageLike = this["--message"],
        server = this["--server"]?.let {
            ChatServer.of(it)
        },
        channel = this["--channel"],
        deletedBy = this["--deletedBy"],
        limit = this["--limit"]?.toIntOrNull() ?: 50,
        type = this["--type"]?.let { runCatching { MessageType.valueOf(it.uppercase()) }.getOrNull() }
    )
}

