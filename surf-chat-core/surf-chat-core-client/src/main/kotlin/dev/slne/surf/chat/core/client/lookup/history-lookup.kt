package dev.slne.surf.chat.core.client.lookup

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickOpensUrl
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.client.message.format.appendLinePrefix
import dev.slne.surf.chat.core.client.util.formatAgo
import dev.slne.surf.chat.core.client.util.formatTime
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.format.TextDecoration
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class RenderData(
    val senderNames: Map<UUID, String>,
    val entry: HistoryEntry
)

val lookupPagination = Pagination<RenderData> {
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
                    if (entry.deleted) {
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
                            error(entry.deletedBy?.toString() ?: "System")
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
                variableValue(entry.messageType.value)
                spacer(")")
            }
        )
    }
}

suspend fun resolveSenderNames(history: List<HistoryEntry>): Map<UUID, String> {
    val senderNames = ConcurrentHashMap<UUID, String>()

    coroutineScope {
        val pending = ObjectOpenHashSet<UUID>()

        for (entry in history) {
            val senderUuid = entry.senderUuid
            if (!pending.add(senderUuid)) continue

            launch {
                val sender = entry.sender()
                senderNames[senderUuid] = sender.lastKnownName ?: senderUuid.toString()
            }
        }
    }

    return senderNames
}

private val rangeRegex = Regex("""(\d+)([smhdw])""", RegexOption.IGNORE_CASE)

private fun parseRangeToMillis(input: String): Long? {
    val match = rangeRegex.matchEntire(input.trim()) ?: return null

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

suspend fun Map<String, String>.parseFilters(): HistoryFilter {
    val senderUuid = this["--sender"]?.let { PlayerLookupService.getUuid(it) }
    val receiverUuid = this["--receiver"]?.let { PlayerLookupService.getUuid(it) }
    val deleted = this["--deleted"]?.toBoolean()
    val deletedBy = this["--deletedBy"]?.let { PlayerLookupService.getUuid(it) }

    return HistoryFilter(
        messageUuid = this["--messageUuid"]?.let { runCatching { UUID.fromString(it) }.getOrNull() },
        senderUuid = senderUuid,
        receiverUuid = receiverUuid,
        messageType = this["--type"]?.let { runCatching { MessageType(it.uppercase()) }.getOrNull() },
        after = this["--range"]?.let {
            parseRangeToMillis(it)?.let { amountToSubtract ->
                OffsetDateTime.now().minus(amountToSubtract, ChronoUnit.MILLIS)
            }
        },
        server = this["--server"],
        deleted = deleted,
        deletedBy = deletedBy,
        limit = this["--limit"]?.toIntOrNull() ?: 50,
    )
}
