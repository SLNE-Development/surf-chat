package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.PageableMessageBuilder
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration

class ChannelListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        integerArgument("page", min = 1, optional = true)
        playerExecutor { player, args ->
            val page = args.getOrDefaultUnchecked("page", 1)

            if(channelService.getAllChannels().isEmpty()) {
                surfChatApi.sendText(player, buildText {
                    error("Es sind keine Kanäle vorhanden.")
                })

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
                            info(when(it.status) {
                                ChannelStatusType.PUBLIC -> "Öffentlich"
                                ChannelStatusType.PRIVATE -> "Privat"
                            })
                            spacer(")")
                            hoverEvent(HoverEvent.showText(createInfoMessage(it)))
                        }
                    }
                }.send(player, page)
            }
        }
    }

    private fun createInfoMessage(channel: ChannelModel): Component {
        return buildText {
            info("ɪɴꜰᴏʀᴍᴀᴛɪᴏɴᴇɴ").appendNewline()
            spacer("ɴᴀᴍᴇ: ").text(channel.name, Colors.WHITE).appendNewline()
            spacer("ʙᴇѕɪᴛᴢᴇʀ: ").text(channel.getOwner().getName(), Colors.WHITE).appendNewline()
            spacer("ᴍᴏᴅᴜѕ: ").text(when(channel.status) {
                ChannelStatusType.PUBLIC -> "Öffentlich"
                ChannelStatusType.PRIVATE -> "Privat"
            }, Colors.WHITE).appendNewline()
            spacer("ᴍɪᴛɢʟɪᴇᴅᴇʀ: ").text(channel.members.size, Colors.WHITE).appendNewline()
            spacer("ᴇɪɴʟᴀᴅᴜɴɢᴇɴ: ").text(channel.invites.size, Colors.WHITE)
        }
    }
}
