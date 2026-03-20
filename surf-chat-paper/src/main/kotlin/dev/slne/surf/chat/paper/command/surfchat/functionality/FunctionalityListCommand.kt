package dev.slne.surf.chat.paper.command.surfchat.functionality

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.util.appendStatusIcon
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.surfapi.bukkit.api.command.executors.anyExecutorSuspend
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.functionalityListCommand() = subcommand("list") {
    withPermission(PermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_LIST)
    integerArgument("page", 1, Int.MAX_VALUE, true)
    anyExecutorSuspend { player, args ->
        val page = args.getOrDefaultUnchecked("page", 1)
        val pagination = Pagination<FunctionalityStatusEntry> {
            title { primary("Chat Funktionalität", TextDecoration.BOLD) }
            rowRenderer { row, _ ->
                listOf(
                    buildText {
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey(row.name)
                        spacer(":")
                        appendSpace()
                        appendStatusIcon(row.status)
                    }
                )
            }
        }

        player.sendText {
            appendInfoPrefix()
            info("Lädt...")
        }


        val content = functionalityService.getFunctionalitiesForAllServers().map { (server, functionalities) ->
            FunctionalityStatusEntry(
                server,
                functionalities.localChatEnabled
            )
        }

        player.sendText {
            append(pagination.renderComponent(content, page))
        }
    }
}


private data class FunctionalityStatusEntry(
    val name: String,
    val status: Boolean
)