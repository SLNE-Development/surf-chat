package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.chat.velocity.command.argument.multiChannelArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.toChatUser
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet

class ChannelSpyCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_SPY)
        withPermission("surf.chat.command.channel.spy")
        multiChannelArgument("channels", optional = true)

        playerExecutor { player, args ->
            val channels = args.getOrDefaultUnchecked("channels", emptyObjectSet<Channel>())

            container.launch {
                val user = player.toChatUser()

                if (channels.isEmpty()) {
                    if (!spyService.isChannelSpying(user)) {
                        player.sendText {
                            appendPrefix()
                            error("Du spionierst in keinem Kanal.")
                        }
                        return@launch
                    }

                    spyService.clearChannelSpys(user)

                    player.sendText {
                        appendPrefix()
                        success("Du spionierst in keinem Kanal mehr.")
                    }
                    return@launch
                }

                channels.forEach {
                    if(!spyService.getChannelSpys(it).contains(user)) {
                        spyService.addChannelSpy(user, it)
                    } else {
                        player.sendText {
                            appendPrefix()
                            error("Du spionierst bereits in dem Kanal ${it.name}.")
                        }
                        return@forEach
                    }
                }

                player.sendText {
                    appendPrefix()
                    success("Du spionierst jetzt in den Kanälen: ")
                    variableValue(channels.joinToString(", ") { it.name })
                }
            }
        }
    }
}