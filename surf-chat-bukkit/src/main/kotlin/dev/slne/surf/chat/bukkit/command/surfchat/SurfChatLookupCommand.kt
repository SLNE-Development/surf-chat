package dev.slne.surf.chat.bukkit.command.surfchat

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.MapArgumentBuilder
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCopiesToClipboard
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

fun CommandAPICommand.surfChatLookupCommand() = subcommand("lookup") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_LOOKUP)
    withAliases("sc", "chat")
    argument(
        MapArgumentBuilder<String, String>("query")
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
        val filter = query.parseFilters()

        val page = query["--page"]?.toIntOrNull() ?: 1

        player.sendText {
            appendPrefix()
            info("Es wird nach Ergebnissen gesucht...")
        }

        plugin.launch {
            if (historyService.isLookupRunning()) {
                player.sendText {
                    appendPrefix()
                    error("Es wird bereits nach Ergebnissen gesucht. Deine Anfrage wird danach fortgesetzt.")
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

            val pagination = Pagination<HistoryEntry> {
                title {
                    info("Suchergebnisse".toSmallCaps(), TextDecoration.BOLD)
                }
                rowRenderer { entry, _ ->
                    listOf(
                        buildText {
                            append(CommonComponents.EM_DASH)
                            appendSpace()
                            variableValue(senderNames[entry.senderUuid] ?: "Unbekannt")
                            spacer(":")
                            appendSpace()
                            variableValue(entry.message)

                            if (entry.deletedBy != null) {
                                appendNewline()
                                appendSpace()
                                appendSpace()
                                appendSpace()
                                append(CommonComponents.EM_DASH)
                                spacer("Gelöscht von ")
                                variableValue(entry.deletedBy ?: "Unbekannt", TextDecoration.ITALIC)
                            }
                            hoverEvent(buildText {
                                info("Klicke, um die Nachrichten Id zu kopieren.")
                            })
                            clickCopiesToClipboard(entry.messageUuid.toString())

                        }
                    )
                }
            }

            player.sendText {
                appendPrefix()
                success("Der Suchvorgang wurde in ")
                variableValue("N/Ams")
                success(" abgeschlossen.")
                appendNewline()
                append(pagination.renderComponent(history, page))
            }
        }
    }
}

private fun Map<String, String>.parseFilters(): HistoryFilter {
    return object : HistoryFilter {
        override val messageUuid: UUID? =
            this@parseFilters["--messageUuid"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        override val senderUuid: UUID? =
            this@parseFilters["--sender"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        override val receiverUuid: UUID? =
            this@parseFilters["--receiver"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        override val messageType: MessageType? =
            this@parseFilters["--type"]?.let { runCatching { MessageType.valueOf(it.uppercase()) }.getOrNull() }
        override val range: Long? =
            this@parseFilters["--range"]?.toLongOrNull()
        override val messageLike: String? =
            this@parseFilters["--message"]
        override val server: String? =
            this@parseFilters["--server"]
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
