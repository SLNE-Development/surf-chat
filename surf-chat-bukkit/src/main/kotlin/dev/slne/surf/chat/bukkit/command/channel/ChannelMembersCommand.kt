package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.util.PageableMessageBuilder
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelMembersCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        integerArgument("page", 1, Int.MAX_VALUE, true)
        playerExecutor { player, args ->
            val page = args.getOrDefaultUnchecked("page", 1)
            val channel: ChannelModel? = channelService.getChannel(player)

            if (channel == null) {
                surfChatApi.sendText(player, buildText {
                    error("Du bist in keinem Nachrichtenkanal.")
                })
                return@playerExecutor
            }

            PageableMessageBuilder {
                pageCommand = "/channel members %page%"

                title {
                    primary("Kanal-Mitglieder von ")
                    info(channel.name)
                }

                line {
                    primary(channel.getOwner().name)
                    darkSpacer(" (Besitzer)")
                }

                channel.getModerators().forEach {
                    line {
                        primary(it.name)
                        darkSpacer(" (Moderator)")
                    }
                }

                channel.getOnlyMembers().forEach {
                    line {
                        primary(it.name)
                        darkSpacer(" (Mitglied)")
                    }
                }
            }.send(player, page)
        }
    }
}
