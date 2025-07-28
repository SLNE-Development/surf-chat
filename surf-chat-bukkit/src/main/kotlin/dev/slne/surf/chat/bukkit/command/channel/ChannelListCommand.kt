package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.ChannelRole
import dev.slne.surf.chat.api.model.ChannelVisibility
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.channelListCommand() = subcommand("list") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_LIST)
    integerArgument("page", min = 1, optional = true)
    playerExecutor { player, args ->
        val page = args.getOrDefaultUnchecked("page", 1)

        if (channelService.getChannels().isEmpty()) {
            player.sendText {
                appendPrefix()
                error("Es sind keine Kanäle vorhanden.")
            }

            return@playerExecutor
        }

        val pagination = Pagination<Channel> {
            title {
                primary("Kanalübersicht".toSmallCaps(), TextDecoration.BOLD)
            }

            rowRenderer { channel, index ->
                listOf(
                    buildText {
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey(channel.channelName)
                        appendSpace()
                        spacer("(")
                        info(
                            when (channel.visibility) {
                                ChannelVisibility.PUBLIC -> "Öffentlich"
                                ChannelVisibility.PRIVATE -> "Privat"
                            }
                        )
                        spacer(")")
                        hoverEvent(createInfoMessage(channel))
                        clickSuggestsCommand("/channel join ${channel.channelName}")
                    }
                )
            }
        }

        player.sendText {
            append(pagination.renderComponent(channelService.getChannels(), page))
        }
    }
}

private fun createInfoMessage(channel: Channel): Component {
    return buildText {
        info("Informationen".toSmallCaps())
        appendNewline {
            append(CommonComponents.EM_DASH)
            appendSpace()
            spacer("Name: ".toSmallCaps())
            variableValue(channel.channelName)
        }

        appendNewline {
            append(CommonComponents.EM_DASH)
            appendSpace()
            spacer("Besitzer: ".toSmallCaps())
            variableValue(
                channel.members.firstOrNull { it.role == ChannelRole.OWNER }?.name ?: "Error"
            )
        }
        appendNewline {
            append(CommonComponents.EM_DASH)
            appendSpace()
            spacer("Modus: ".toSmallCaps())
            variableValue(
                when (channel.visibility) {
                    ChannelVisibility.PUBLIC -> "Öffentlich"
                    ChannelVisibility.PRIVATE -> "Privat"
                }
            )
        }
        appendNewline {
            append(CommonComponents.EM_DASH)
            appendSpace()
            spacer("Mitglieder: ".toSmallCaps())
            variableValue(channel.members.size)
        }
        appendNewline {
            append(CommonComponents.EM_DASH)
            appendSpace()
            spacer("Einladungen: ".toSmallCaps())
            variableValue(channel.invitedPlayers.size)
        }
    }
}
