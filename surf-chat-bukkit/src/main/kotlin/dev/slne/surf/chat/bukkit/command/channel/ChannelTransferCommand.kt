package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.bukkit.command.argument.ChannelMembersArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.Component

import org.bukkit.OfflinePlayer

class ChannelTransferCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_TRANSFER)
        withArguments(ChannelMembersArgument("member"))

        playerExecutor { player, args ->
            val channel: Channel? = channelService.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("member") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (channel == null) {
                    user.sendPrefixed{
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.isOwner(user)) {
                    user.sendPrefixed{
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (!channel.isMember(targetUser)) {
                    user.sendPrefixed{
                        error("Der Spieler ")
                        variableValue(target.name ?: target.uniqueId.toString())
                        error(" ist kein Mitglied in deinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                val confirmComponent: Component = buildText {
                    info("Klicke hier, um den Vorgang zu bestätigen. ")
                    success("[BESTÄTIGEN]")
                    hoverEvent(buildText {
                        info("Klicke hier, um den Vorgang zu bestätigen.")
                    })
                    clickEvent(ClickEvent.callback {
                        plugin.launch {
                            if (channel.members.none { it.value == ChannelRole.OWNER }) {
                                user.sendPrefixed {
                                    error("Der Nachrichtenkanal benötigt mindestens einen Besitzer.")
                                }
                                return@launch
                            }

                            channel.transferOwnership(targetUser)

                            user.sendPrefixed {
                                success("Du hast den Nachrichtenkanal ")
                                variableValue(channel.name)
                                success(" an ")
                                variableValue(target.name ?: target.uniqueId.toString())
                                success(" übertragen.")
                            }

                            targetUser.sendPrefixed {
                                info("Du bist jetzt der Besitzer des Nachrichtenkanals ")
                                variableValue(channel.name)
                                info(".")
                            }
                        }
                    })
                }

                user.sendPrefixed {
                    append(confirmComponent)
                }
            }
        }
    }
}
