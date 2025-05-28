package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.multiLiteralArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelStatus
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.edit
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService

class ChannelPrivacyModeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_MODE)
        multiLiteralArgument(nodeName = "mode", MODE_PUBLIC, MODE_PRIVATE)
        playerExecutor { player, args ->
            val channel: Channel? = channelService.getChannel(player)

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (channel == null) {
                    user.sendPrefixed{ error("Du bist in keinem Nachrichtenkanal.") }
                    return@launch
                }

                if (!channel.isOwner(user)) {
                    user.sendPrefixed { error("Du verfügst nicht über die erforderliche Berechtigung.") }
                    return@launch
                }

                val mode: String by args

                when (mode) {
                    MODE_PUBLIC -> {
                        if (channel.status == ChannelStatus.PUBLIC) {
                            user.sendPrefixed { error("Der Nachrichtenkanal ist bereits öffentlich.") }
                            return@launch
                        }

                        channel.edit {
                            status = ChannelStatus.PUBLIC
                        }

                        user.sendPrefixed {
                            info("Der Nachrichtenkanal ")
                            variableValue(channel.name)
                            info(" ist nun öffentlich.")
                        }
                    }

                    MODE_PRIVATE -> {
                        if (channel.status == ChannelStatus.PRIVATE) {
                            user.sendPrefixed { error("Der Nachrichtenkanal ist bereits privat.") }
                            return@launch
                        }

                        channel.edit {
                            status = ChannelStatus.PRIVATE
                        }

                        user.sendPrefixed {
                            info("Der Nachrichtenkanal ")
                            variableValue(channel.name)
                            info(" ist nun privat.")
                        }
                    }

                    else -> {
                        user.sendPrefixed { error("Der Kanal-Modus '$mode' wurde nicht gefunden.") }
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
