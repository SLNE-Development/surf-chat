package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.service.ChatHistoryService
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class SurfChatChatClearCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.clear")
        playerExecutor{ player, _ ->
            ChatHistoryService.clearChat()

            SurfChat.send(player, MessageBuilder().primary("Der Chat wurde ").success("geleert."))
        }
    }
}
