package dev.slne.surf.chat.bukkit.command.denylist.action

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.denylistActionListCommand() = subcommand("list") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ACTION_LIST)
    integerArgument("page", min = 1, max = Int.MAX_VALUE, optional = true)
    anyExecutor { executor, args ->
        val page = args.getOrDefaultUnchecked("page", 1)
        val localDenylistActions = denylistActionService.listLocalActions()

        if (localDenylistActions.isEmpty()) {
            executor.sendText {
                appendPrefix()
                error("Es sind keine Aktionen in der internen Aktionsliste vorhanden.")
            }
            return@anyExecutor
        }

        val pagination = Pagination<DenylistAction> {
            title {
                primary("Interne Aktionsliste".toSmallCaps(), TextDecoration.BOLD)
            }

            rowRenderer { entry, _ ->
                listOf(
                    buildText {
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey(entry.name)
                        appendSpace()
                        spacer("(${entry.actionType})")
                        hoverEvent(buildText {
                            append(CommonComponents.EM_DASH)
                            appendSpace()
                            variableKey("Grund")
                            spacer(":")
                            appendSpace()
                            variableValue(entry.reason)
                        })
                    }
                )
            }
        }

        executor.sendText {
            append(pagination.renderComponent(localDenylistActions, page))
        }
    }
}