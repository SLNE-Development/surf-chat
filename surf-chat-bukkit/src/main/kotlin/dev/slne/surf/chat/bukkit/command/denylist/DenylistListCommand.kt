package dev.slne.surf.chat.bukkit.command.denylist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.util.unixTime
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.bukkit.api.command.executors.anyExecutorSuspend
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import dev.slne.surf.surfapi.core.api.util.mapAsync
import net.kyori.adventure.text.format.TextDecoration

private val pagination = Pagination<PaginationData> {
    title {
        primary("Interne Denylist".toSmallCaps(), TextDecoration.BOLD)
    }

    rowRenderer { (addedByName, entry), _ ->
        listOf(
            buildText {
                append(CommonComponents.EM_DASH)
                appendSpace()
                variableKey(entry.word)
                appendSpace()
                spacer("(${addedByName ?: entry.addedBy ?: "#System"})")
                hoverEvent(buildText {
                    append(CommonComponents.EM_DASH)
                    appendSpace()
                    variableKey("Grund")
                    spacer(":")
                    appendSpace()
                    variableValue(entry.reason)
                    appendNewline()
                    append(CommonComponents.EM_DASH)
                    appendSpace()
                    variableKey("Datum")
                    spacer(":")
                    appendSpace()
                    variableValue(entry.addedAt.unixTime())
                })
            }
        )
    }
}

fun CommandAPICommand.denylistListCommand() = subcommand("list") {
    withPermission(PermissionRegistry.COMMAND_DENYLIST_LIST)
    integerArgument("page", min = 1, max = Int.MAX_VALUE, optional = true)
    anyExecutorSuspend { executor, args ->
        val page = args.getOrDefaultUnchecked("page", 1)
        val denylistEntries = denylistService.getLocalEntries()

        if (denylistEntries.isEmpty()) {
            executor.sendText {
                appendErrorPrefix()
                error("Es sind keine Einträge in der internen Denylist vorhanden.")
            }
            return@anyExecutorSuspend
        }

        val data = denylistEntries.mapAsync { entry ->
            val addedByName = entry.addedBy()?.lastKnownName
            PaginationData(addedByName, entry)
        }

        executor.sendText {
            append(pagination.renderComponent(data, page))
        }
    }
}

private data class PaginationData(
    val addedByName: String?,
    val entry: DenylistEntry,
)