package dev.slne.surf.chat.bukkit.command.lookup

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.MapArgumentBuilder
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.formatAgo
import dev.slne.surf.chat.bukkit.util.formatTime
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.util.appendLinePrefix
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
    argument(
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
        val query: Map<String, String> by args

        player.sendText {
            appendPrefix()
            info("Es wird nach Ergebnissen gesucht...")
        }

        plugin.launch {

            val filter = query.parseFilters()
            val page = query["--page"]?.toIntOrNull() ?: 1

            if (historyService.isLookupRunning()) {
                player.sendText {
                    appendPrefix()
                    warning("Es wird bereits nach Ergebnissen gesucht. Deine Anfrage wird danach fortgesetzt.")
                }
            }
            val history =
                historyService.findHistoryEntry(filter).sortedByDescending { it.sentAt }

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

            val receiverNames = history.mapNotNull { it.receiverUuid }
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
                                appendSpace()

                                hoverEvent(buildText {
                                    spacer(entry.sentAt.formatTime())
                                })
                            }
                            spacer("-")
                            appendSpace()
                            append {
                                val name = senderNames[entry.senderUuid] ?: "Unbekannt"
                                variableValue(name)
                                hoverEvent(buildText {
                                    spacer("Klicke, um das Profil zu öffnen.")
                                })
                                clickOpensUrl("https://laby.net/$name")
                            }
                            appendSpace()
                            append {
                                info("schrieb")
                                hoverEvent(buildText {
                                    info("auf Server ")
                                    variableValue(entry.server.name)

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

                                hoverEvent(buildText {
                                    text(entry.message, Colors.WHITE)
                                })
                            }

                            if (entry.deletedBy != null) {
                                decorate(TextDecoration.STRIKETHROUGH)
                            }
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

    return object : HistoryFilter {
        override val messageUuid: UUID? =
            this@parseFilters["--messageUuid"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        override val senderUuid: UUID? = senderUuid
        override val receiverUuid: UUID? = receiverUuid
        override val messageType: MessageType? =
            this@parseFilters["--type"]?.let { runCatching { MessageType.valueOf(it.uppercase()) }.getOrNull() }
        override val range: Long? =
            this@parseFilters["--range"]?.let { parseRangeToMillis(it) }
        override val messageLike: String? =
            this@parseFilters["--message"]
        override val server: ChatServer? = this@parseFilters["--server"]?.let {
            ChatServer.of(it)
        }
        override val channel: String? =
            this@parseFilters["--channel"]
        override val deletedBy: String? =
            this@parseFilters["--deletedBy"]
        override val type: MessageType? =
            this@parseFilters["--type"]?.let { runCatching { MessageType.valueOf(it.uppercase()) }.getOrNull() }
        override val limit: Int? =
            this@parseFilters["--limit"]?.toIntOrNull()
    }
}

