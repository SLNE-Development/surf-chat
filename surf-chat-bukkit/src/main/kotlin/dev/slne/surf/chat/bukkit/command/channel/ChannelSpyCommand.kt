package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.command.argument.multiChannelArgument
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet

class ChannelSpyCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_SPY)
        withPermission("surf.chat.command.channel.spy")
        multiChannelArgument("channels", optional = true)

        playerExecutor { player, args ->
            val channels = args.getOrDefaultUnchecked("channels", emptyObjectSet<ChannelModel>())

            if (channels.isEmpty()) {
                if (!spyService.isChannelSpying(player.uniqueId)) {
                    player.sendPrefixed {
                        error("Du spionierst in keinem Kanal.")
                    }
                    return@playerExecutor
                }

                spyService.clearChannelSpies(player.uniqueId)

                player.sendPrefixed {
                    success("Du spionierst in keinem Kanal mehr.")
                }
                return@playerExecutor
            }

            channels.forEach {
                if(!spyService.getChannelSpies(it).contains(player.uniqueId)) {
                    spyService.addChannelSpy(player.uniqueId, it)
                } else {
                    player.sendPrefixed {
                        error("Du spionierst bereits in dem Kanal ${it.name}.")
                    }
                    return@playerExecutor
                }
            }

            player.sendPrefixed {
                success("Du spionierst jetzt in den Kanälen: ")
                variableValue(channels.joinToString(", ") { it.name })
            }
        }
    }
}