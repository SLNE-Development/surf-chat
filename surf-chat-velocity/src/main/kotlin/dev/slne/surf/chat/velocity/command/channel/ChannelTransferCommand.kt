package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.command.argument.playerArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChannelMember
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.Component

class ChannelTransferCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_TRANSFER)
        playerArgument("member")

        playerExecutor { player, args ->
            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val channel: Channel? = channelService.getChannel(user)
                val target = args.getUnchecked<Player>("member") ?: return@launch


                if (channel == null) {
                    user.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                val targetUser = databaseService.getUser(target.uniqueId)

                val channelMember = user.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Du bist in diesem Nachrichtenkanal nicht registriert.")
                    }
                    return@launch
                }

                val targetChannelMember = targetUser.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Du bist in diesem Nachrichtenkanal nicht registriert.")
                    }
                    return@launch
                }

                if (!channel.isOwner(channelMember)) {
                    user.sendText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (!channel.isMember(targetUser)) {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(target.username ?: target.uniqueId.toString())
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
                        container.launch {
                            if (channel.members.none { it.role == ChannelRole.OWNER }) {
                                user.sendText  {
                                    error("Der Nachrichtenkanal benötigt mindestens einen Besitzer.")
                                }
                                return@launch
                            }

                            channel.transferOwnership(targetChannelMember)

                            user.sendText {
                                success("Du hast den Nachrichtenkanal ")
                                variableValue(channel.name)
                                success(" an ")
                                variableValue(target.username ?: target.uniqueId.toString())
                                success(" übertragen.")
                            }

                            targetUser.sendText {
                                info("Du bist jetzt der Besitzer des Nachrichtenkanals ")
                                variableValue(channel.name)
                                info(".")
                            }
                        }
                    })
                }

                user.sendText {
                    append(confirmComponent)
                }
            }
        }
    }
}
