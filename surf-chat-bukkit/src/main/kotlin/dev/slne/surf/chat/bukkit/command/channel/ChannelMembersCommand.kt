package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.bukkit.command.argument.channelArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.channelMembersCommand() = subcommand("members") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_MEMBERS)
    channelArgument("channel", true)
    integerArgument("page", 1, optional = true)
    playerExecutor { player, args ->
        val page = args.getOrDefaultUnchecked("page", 1)
        val user = player.user() ?: return@playerExecutor
        val channel: Channel? =
            args.getOrDefaultUnchecked("channel", channelService.getChannel(user))

        if (channel == null) {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal oder der Kanal existiert nicht.")
            }
            return@playerExecutor
        }

        val pagination = Pagination<ChannelMember> {
            title {
                primary("Kanalmitglieder".toSmallCaps(), TextDecoration.BOLD)
            }

            rowRenderer { member, index ->
                listOf(
                    buildText {
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey(member.name)
                        appendSpace()
                        spacer("(")
                        variableValue(member.role.name.replaceFirstChar { it.uppercase() })
                        spacer(")")
                    }
                )
            }
        }

        player.sendText {
            append(pagination.renderComponent(channel.members.sortedBy { it.role }, page))
        }
    }
}
