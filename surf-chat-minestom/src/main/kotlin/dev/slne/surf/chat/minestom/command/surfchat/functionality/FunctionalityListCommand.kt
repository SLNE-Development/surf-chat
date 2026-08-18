package dev.slne.surf.chat.minestom.command.surfchat.functionality

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.integerArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.chat.core.client.message.format.appendStatusIcon
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.common.service.FunctionalityService
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.functionalityListCommand(): CommandAPICommand = withSubcommand(
    subcommand("list") {
        withPermission(ChatPermissions.COMMAND_SURFCHAT_FUNCTIONALITY_LIST)
        integerArgument("page", 1, Int.MAX_VALUE, true)
        anyExecutorSuspend { player, args ->
            val page = args.getOptional<Int>("page") ?: 1
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


            val content = FunctionalityService.getFunctionalitiesForAllServers().map { (server, functionalities) ->
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
)


private data class FunctionalityStatusEntry(
    val name: String,
    val status: Boolean
)
