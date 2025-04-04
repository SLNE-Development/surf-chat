package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class ChannelInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withOptionalArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getOrDefaultUnchecked<Channel?>("channel", Channel.getChannel(player)) ?: return@playerExecutor

            player.sendMessage(createInfoMessage(channel))
        }
    }

    private fun createInfoMessage(channel: Channel): Component {
        val owner = channel.owner ?: return MessageBuilder().error("Ein Fehler ist aufgetreten.").build()
        return MessageBuilder()
            .primary("Kanalinformation: ").info(channel.name).newLine()
            .darkSpacer("   - ").variableKey("Beschreibung: ").variableValue(channel.description)
            .newLine()
            .darkSpacer("   - ").variableKey("Besitzer: ").variableValue(Bukkit.getOfflinePlayer(owner).name ?: "Unbekannt")
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
