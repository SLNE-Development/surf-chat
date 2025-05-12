package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.chat.bukkit.util.PageableMessageBuilder
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.format.TextDecoration

class ChannelMembersCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelArgument("channel").setOptional(true))
        integerArgument("page", 1, Int.MAX_VALUE, true)
        playerExecutor { player, args ->
            val page = args.getOrDefaultUnchecked("page", 1)
            val channel: ChannelModel? = args.getOrDefaultUnchecked("channel", channelService.getChannel(player))

            if (channel == null) {
                surfChatApi.sendText(player, buildText {
                    error("Du bist in keinem Nachrichtenkanal oder der Kanal existiert nicht.")
                })
                return@playerExecutor
            }

            PageableMessageBuilder {
                pageCommand = "/channel members ${channel.name} %page%"

                title {
                    info("ᴍɪᴛɢʟɪᴇᴅᴇʀ ᴠᴏɴ ")
                    variableValue(channel.name)
                }

                line {
                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableValue(channel.getOwner().getName())
                    darkSpacer(" (Besitzer)")
                }

                channel.getModerators().forEach {
                    line {
                        append {
                            info("| ")
                            decorate(TextDecoration.BOLD)
                        }
                        variableValue(it.getName())
                        darkSpacer(" (Moderator)")
                    }
                }

                channel.getOnlyMembers().forEach {
                    line {
                        append {
                            info("| ")
                            decorate(TextDecoration.BOLD)
                        }
                        variableValue(it.getName())
                        darkSpacer(" (Mitglied)")
                    }
                }
            }.send(player, page)
        }
    }
}
