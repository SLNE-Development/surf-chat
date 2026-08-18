package dev.slne.surf.chat.minestom.command

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.command.CommandRegistrar
import dev.slne.surf.chat.minestom.command.direct.directMessageCommand
import dev.slne.surf.chat.minestom.command.direct.replyCommand
import dev.slne.surf.chat.minestom.command.ignore.ignoreCommand
import dev.slne.surf.chat.minestom.command.spy.directMessageSpyCommand
import dev.slne.surf.chat.minestom.command.surfchat.surfChatCommand

/**
 * Registers the chat commands of this plugin.
 */
class ChatCommandRegistrar @Inject constructor() : CommandRegistrar {
    override fun register() {
        surfChatCommand()
        teamchatCommand()
        ignoreCommand()
        directMessageSpyCommand()
        slowChatCommand()
        directMessageCommand()
        replyCommand()
    }
}
