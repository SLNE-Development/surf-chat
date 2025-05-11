package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.multiLiteralArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.edit
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelPrivacyModeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {

        multiLiteralArgument(nodeName = "mode", "public", "private")
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (channel == null) {
                    user.sendText(buildText { error("Du bist in keinem Nachrichtenkanal.") })
                    return@launch
                }

                if (!channel.isOwner(user)) {
                    user.sendText(buildText { error("Du verfügst nicht über die erforderliche Berechtigung.") })
                    return@launch
                }

                val mode: String by args

                when(mode) {
                    "public" -> {
                        channel.edit {
                            status = ChannelStatusType.PUBLIC
                        }

                        user.sendText(buildText {
                            info("Der Nachrichtenkanal ")
                            variableValue(channel.name)
                            info(" ist nun öffentlich.")
                        })
                    }
                    "private" -> {
                        channel.edit {
                            status = ChannelStatusType.PRIVATE
                        }

                        user.sendText(buildText {
                            info("Der Nachrichtenkanal ")
                            variableValue(channel.name)
                            info(" ist nun privat.")
                        })
                    }

                    else -> {
                        user.sendText(buildText { error("Der Kanal-Modus '$mode' wurde nicht gefunden.") })
                    }
                }
            }
        }
    }
}
