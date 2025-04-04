package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.provider.ChannelProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.PageableMessageBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        integerArgument("page", 1, Int.MAX_VALUE, true)
        playerExecutor { player, args ->
            val message = PageableMessageBuilder()
            val page = args.getOrDefaultUnchecked("page", 1)

            var index = 0

            message.setPageCommand("/channel list %page%")

            for (channel in ChannelProvider.channels.values) {
                index++

                message.addLine(
                    MessageBuilder().variableValue("$index. ").primary(channel.name)
                        .darkSpacer(" (")
                        .info((channel.members.size + channel.moderators.size + 1).toString())
                        .darkSpacer(")").build().hoverEvent(
                            this.createInfoMessage(channel)
                        )
                )
            }
            message.send(player, page)
        }
    }

    private fun createInfoMessage(channel: Channel): Component {
        val owner = channel.owner ?: return MessageBuilder().error("Ein Fehler ist aufgetreten.").build()
        val ownerPlayer = Bukkit.getOfflinePlayer(owner)
        return MessageBuilder()
            .primary("Kanalinformation: ").info(channel.name).newLine()
            .darkSpacer("   - ").variableKey("Beschreibung: ").variableValue(channel.description)
            .newLine()
            .darkSpacer("   - ").variableKey("Besitzer: ").variableValue(ownerPlayer.name ?: ownerPlayer.uniqueId.toString())
            .newLine()
            .darkSpacer("   - ").variableKey("Status: ")
            .variableValue(if (channel.closed) "Geschlossen" else "Offen").newLine()
            .darkSpacer("   - ").variableKey("Mitglieder: ")
            .variableValue((channel.members.size + channel.moderators.size + 1).toString())
            .newLine()
            .darkSpacer("   - ").variableKey("Einladungen: ")
            .variableValue(channel.invites.size.toString()).newLine()
            .build()
    }
}
