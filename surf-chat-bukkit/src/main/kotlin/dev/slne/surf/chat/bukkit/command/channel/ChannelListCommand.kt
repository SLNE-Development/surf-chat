package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.util.PageableMessageBuilder
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent

class ChannelListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        integerArgument("page", min = 1, optional = true)
        playerExecutor { player, args ->
            val page = args.getOrDefaultUnchecked("page", 1)

            if(channelService.getAllChannels().isEmpty()) {
                surfChatApi.sendText(player, buildText {
                    error("Es sind keine Kanäle vorhanden.")
                })

                return@playerExecutor
            }

            PageableMessageBuilder {
                pageCommand = "/channel list %page%"
                title {
                    primary("Kanalübersicht")
                }

                channelService.getAllChannels().forEach {
                    line {
                        spacer(" - ")
                        variableValue("${it.name} ")
                        darkSpacer("(")
                        variableValue(when(it.status) {
                            ChannelStatusType.PUBLIC -> "Öffentlich"
                            ChannelStatusType.PRIVATE -> "Privat"
                        })
                        darkSpacer(") ")
                        hoverEvent(HoverEvent.showText(createInfoMessage(it)))
                    }
                }
            }.send(player, page)
        }
    }

    private fun createInfoMessage(channel: ChannelModel): Component {
        return buildText {
            primary("Kanalinformation: ").info(channel.name)
            appendNewline()
            darkSpacer("   - ").variableKey("Besitzer: ")
            variableValue(channel.getOwner().getName())
            appendNewline()
            darkSpacer("   - ").variableKey("Status: ")
            variableValue(when(channel.status) {
                ChannelStatusType.PUBLIC -> "Öffentlich"
                ChannelStatusType.PRIVATE -> "Privat"
            })
            appendNewline()
            darkSpacer("   - ").variableKey("Mitglieder: ")
            variableValue(channel.members.size.toString())
            appendNewline()
            darkSpacer("   - ").variableKey("Einladungen: ")
            variableValue(channel.invites.size.toString())
            appendNewline()
        }
    }
}
