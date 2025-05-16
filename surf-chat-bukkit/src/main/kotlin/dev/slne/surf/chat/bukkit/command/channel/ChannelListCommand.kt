package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.PageableMessageBuilder
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration

class ChannelListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_LIST)
        integerArgument("page", min = 1, optional = true)
        playerExecutor { player, args ->
            val page = args.getOrDefaultUnchecked("page", 1)

            if (channelService.getAllChannels().isEmpty()) {
                player.sendPrefixed {
                    error("Es sind keine Kanäle vorhanden.")
                }

                return@playerExecutor
            }

            plugin.launch {
                PageableMessageBuilder {
                    pageCommand = "/channel list %page%"
                    title {
                        info("ᴋᴀɴᴀʟüʙᴇʀѕɪᴄʜᴛ")
                    }

                    channelService.getAllChannels().forEach {
                        line {
                            append {
                                info("| ")
                                decorate(TextDecoration.BOLD)
                            }
                            text(it.name, Colors.WHITE)
                            spacer(" (")
                            info(
                                when (it.status) {
                                    ChannelStatusType.PUBLIC -> "Öffentlich"
                                    ChannelStatusType.PRIVATE -> "Privat"
                                }
                            )
                            spacer(")")
                            hoverEvent(createInfoMessage(it))
                        }
                    }
                }.send(player, page)
            }
        }
    }

    private fun createInfoMessage(channel: ChannelModel): Component {
        return buildText {
            info("Informationen".toSmallCaps()).appendNewline()
            spacer("Name: ".toSmallCaps()).text(channel.name, Colors.WHITE).appendNewline()
            spacer("Besitzer: ".toSmallCaps()).text(channel.getOwner().getName(), Colors.WHITE).appendNewline()
            spacer("Modus: ".toSmallCaps()).text(
                when (channel.status) {
                    ChannelStatusType.PUBLIC -> "Öffentlich"
                    ChannelStatusType.PRIVATE -> "Privat"
                }, Colors.WHITE
            ).appendNewline()
            spacer("Mitglieder: ".toSmallCaps()).text(channel.members.size, Colors.WHITE).appendNewline()
            spacer("Einladungen: ".toSmallCaps()).text(channel.invites.size, Colors.WHITE)
        }
    }
}
