package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.velocity.command.argument.channelArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.PageableMessageBuilder
import dev.slne.surf.chat.velocity.util.toChatUser
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.format.TextDecoration

class ChannelMembersCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        channelArgument("channel", optional = true)
        integerArgument("page", 1, optional = true)
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_MEMBERS)
        playerExecutor { player, args ->
            container.launch {
                val page = args.getOrDefaultUnchecked("page", 1)
                val channel: Channel? =
                    args.getOrDefaultUnchecked("channel", channelService.getChannel(player.toChatUser()))

                if (channel == null) {
                    player.sendText {
                        error("Du bist in keinem Nachrichtenkanal oder der Kanal existiert nicht.")
                    }
                    return@launch
                }

                PageableMessageBuilder {
                    pageCommand = "/channel members ${channel.name} %page%"

                    title {
                        info("Mitglider von ".toSmallCaps())
                        variableValue(channel.name)
                    }

                    line {
                        append {
                            info("| ")
                            decorate(TextDecoration.BOLD)
                        }
                        variableValue(channel.getOwner().name)
                        darkSpacer(" (Besitzer)")
                    }

                    channel.members.filter { it.role == ChannelRole.MODERATOR }.forEach {
                        line {
                            append {
                                info("| ")
                                decorate(TextDecoration.BOLD)
                            }
                            variableValue(it.name)
                            darkSpacer(" (Moderator)")
                        }
                    }

                    channel.members.filter { it.role == ChannelRole.MEMBER }.forEach {
                        line {
                            append {
                                info("| ")
                                decorate(TextDecoration.BOLD)
                            }
                            variableValue(it.name)
                            darkSpacer(" (Mitglied)")
                        }
                    }
                }.send(player, page)
            }
        }
    }
}
