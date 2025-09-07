package dev.slne.surf.chat.bukkit.command.denylist.action

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.unixTime
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.denylistListCommand() = subcommand("list") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_LIST)
    integerArgument("page", min = 1, max = Int.MAX_VALUE, optional = true)
    anyExecutor { executor, args ->
        val page = args.getOrDefaultUnchecked("page", 1)
        val denylistEntries = denylistService.getLocalEntries()

        if (denylistEntries.isEmpty()) {
            executor.sendText {
                appendPrefix()
                error("Es sind keine Einträge in der internen Denylist vorhanden.")
            }
            return@anyExecutor
        }

        val pagination = Pagination<DenylistEntry> {
            title {
                primary("Interne Denylist".toSmallCaps(), TextDecoration.BOLD)
            }

            rowRenderer { entry, _ ->
                listOf(
                    buildText {
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey(entry.word)
                        appendSpace()
                        spacer("(${entry.addedBy})")
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

        executor.sendText {
            append(pagination.renderComponent(denylistEntries, page))
        }
    }
}