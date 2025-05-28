package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.multiLiteralArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelStatus
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChannelMember

class ChannelPrivacyModeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_MODE)
        multiLiteralArgument(nodeName = "mode", MODE_PUBLIC, MODE_PRIVATE)
        playerExecutor { player, args ->

            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val channel: Channel? = channelService.getChannel(user)

                if (channel == null) {
                    user.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.isOwner(user.toChannelMember(channel) ?: run {
                        user.sendText {
                            error("Du bist nicht der Besitzer dieses Nachrichtenkanals.")
                        }
                        return@launch
                    })) {
                    user.sendText { error("Du verfügst nicht über die erforderliche Berechtigung.") }
                    return@launch
                }

                val mode: String by args

                when (mode) {
                    MODE_PUBLIC -> {
                        if (channel.status == ChannelStatus.PUBLIC) {
                            user.sendText { error("Der Nachrichtenkanal ist bereits öffentlich.") }
                            return@launch
                        }

                        channel.status = ChannelStatus.PUBLIC

                        user.sendText {
                            info("Der Nachrichtenkanal ")
                            variableValue(channel.name)
                            info(" ist nun öffentlich.")
                        }
                    }

                    MODE_PRIVATE -> {
                        if (channel.status == ChannelStatus.PRIVATE) {
                            user.sendText { error("Der Nachrichtenkanal ist bereits privat.") }
                            return@launch
                        }

                        channel.status = ChannelStatus.PRIVATE

                        user.sendText {
                            info("Der Nachrichtenkanal ")
                            variableValue(channel.name)
                            info(" ist nun privat.")
                        }
                    }

                    else -> {
                        user.sendText {
                            error("Der Kanal-Modus '$mode' wurde nicht gefunden.")
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val MODE_PUBLIC = "public"
        private const val MODE_PRIVATE = "private"
    }
}
