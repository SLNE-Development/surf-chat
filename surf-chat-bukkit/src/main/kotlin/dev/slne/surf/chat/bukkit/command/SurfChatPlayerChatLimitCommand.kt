package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.provider.ConfigurationProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.sendText

class SurfChatPlayerChatLimitCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        integerArgument("limit", min = 0)
        playerExecutor { player, args ->
            val limit: Int by args

            ConfigurationProvider.setMinimalPlayersUntilMessageBlock(limit)

            player.sendText(MessageBuilder().primary("Du hast das ").info("Chatlimit").primary(" auf ").info(limit.toString()).success(" gesetzt."))
        }
    }
}