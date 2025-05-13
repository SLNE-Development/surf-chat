package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.command.argument.multiChannelArgument
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.emptyObjectList

class ChannelSpyCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel.spy")
        multiChannelArgument("channels", optional = true)

        playerExecutor { player, args ->
            val channels = args.getOrDefaultUnchecked("channels", emptyObjectList<ChannelModel>())

            if(channels.isEmpty()) {
                if(!spyService.isChannelSpying(player)) {
                    surfChatApi.sendText(player, buildText {
                        error("Du spionierst in keinem Kanal.")
                    })
                    return@playerExecutor
                }

                spyService.clearChannelSpys(player)

                surfChatApi.sendText(player, buildText {
                    success("Du spionierst in keinem Kanal mehr.")
                })
            }

            channels.forEach {
                spyService.addChannelSpy(player, it)
            }

            surfChatApi.sendText(player, buildText {
                success("Du spionierst jetzt in den Kanälen: ")
                variableValue(channels.joinToString(", ") { it.name })
            })
        }
    }
}