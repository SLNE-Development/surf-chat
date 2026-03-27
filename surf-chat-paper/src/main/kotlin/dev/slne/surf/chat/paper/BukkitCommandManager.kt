package dev.slne.surf.chat.paper

import dev.slne.surf.chat.paper.command.direct.directMessageCommand
import dev.slne.surf.chat.paper.command.direct.replyCommand
import dev.slne.surf.chat.paper.command.ignore.ignoreCommand
import dev.slne.surf.chat.paper.command.spy.directMessageSpyCommand
import dev.slne.surf.chat.paper.command.surfchat.surfChatCommand
import dev.slne.surf.chat.paper.command.teamchatCommand

object BukkitCommandManager {
    fun registerCommands() {
        surfChatCommand()
        teamchatCommand()
        ignoreCommand()
        directMessageSpyCommand()
        directMessageCommand()
        replyCommand()
    }
}