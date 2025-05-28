package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelStatus
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.PageableMessageBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

class ChannelListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_LIST)
        integerArgument("page", min = 1, optional = true)
        playerExecutor { player, args ->
            val page = args.getOrDefaultUnchecked("page", 1)

            if (channelService.getAllChannels().isEmpty()) {
                player.sendText {
                    error("Es sind keine Kanäle vorhanden.")
                }

                return@playerExecutor
            }

            container.launch {
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
                                    ChannelStatus.PUBLIC -> "Öffentlich"
                                    ChannelStatus.PRIVATE -> "Privat"
                                }
                            )
                            spacer(")")
                            hoverEvent(createInfoMessage(it))
                            clickSuggestsCommand("/channel join ${it.name}")
                        }
                    }
                }.send(player, page)
            }
        }
    }

    private fun createInfoMessage(channel: Channel): Component {
        return buildText {
            info("Informationen".toSmallCaps()).appendNewline()
            spacer("Name: ".toSmallCaps()).text(channel.name, Colors.WHITE).appendNewline()
            spacer("Besitzer: ".toSmallCaps()).text(channel.getOwner().name, Colors.WHITE).appendNewline()
            spacer("Modus: ".toSmallCaps()).text(
                when (channel.status) {
                    ChannelStatus.PUBLIC -> "Öffentlich"
                    ChannelStatus.PRIVATE -> "Privat"
                }, Colors.WHITE
            ).appendNewline()
            spacer("Mitglieder: ".toSmallCaps()).text(channel.members.size, Colors.WHITE).appendNewline()
            spacer("Einladungen: ".toSmallCaps()).text(channel.invites.size, Colors.WHITE)
        }
    }
}
