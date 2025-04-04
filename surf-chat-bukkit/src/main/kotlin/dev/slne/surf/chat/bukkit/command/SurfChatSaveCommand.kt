package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.plugin
import dev.slne.surf.social.chat.service.DatabaseService
import dev.slne.surf.social.chat.util.MessageBuilder

class SurfChatSaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.save")
        playerExecutor{ player, _ ->
            plugin.launch {
                ChatUser.cache.asMap().values.forEach { user ->
                    DatabaseService.saveUser(user)
                }

                SurfChat.send(player, MessageBuilder().success("Alle Nutzer wurden gespeichert."))
            }
        }
    }
}