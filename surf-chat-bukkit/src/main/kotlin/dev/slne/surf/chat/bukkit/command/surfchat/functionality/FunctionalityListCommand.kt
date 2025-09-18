package dev.slne.surf.chat.bukkit.command.surfchat.functionality

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.appendStatusIcon
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.functionalityListCommand() = subcommand("list") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_LIST)
    integerArgument("page", 1, Int.MAX_VALUE, true)
    anyExecutor { player, args ->
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
            appendPrefix()
            info("Lädt...")
        }

        plugin.launch {
            val content = functionalityService.getAllServers().map {
                FunctionalityStatusEntry(
                    it.name,
                    functionalityService.isEnabledForServer(it)
                )
            }

            player.sendText {
                append(pagination.renderComponent(content, page))
            }
        }
    }
}


private data class FunctionalityStatusEntry(
    val name: String,
    val status: Boolean
)