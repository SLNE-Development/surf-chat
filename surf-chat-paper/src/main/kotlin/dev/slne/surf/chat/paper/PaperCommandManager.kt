package dev.slne.surf.chat.paper

import dev.slne.surf.chat.paper.channel.command.channelAdminCommand
import dev.slne.surf.chat.paper.channel.command.channelCommand
import dev.slne.surf.chat.paper.command.denylist.action.denylistActionCommand
import dev.slne.surf.chat.paper.command.denylist.denylistCommand
import dev.slne.surf.chat.paper.command.ignore.ignoreCommand
import dev.slne.surf.chat.paper.command.spy.directMessageSpyCommand
import dev.slne.surf.chat.paper.command.surfchat.surfChatCommand
import dev.slne.surf.chat.paper.command.teamchatCommand

object PaperCommandManager {
    fun registerCommands() {
        surfChatCommand()
        teamchatCommand()
        channelCommand()
        channelAdminCommand()
        denylistCommand()
        denylistActionCommand()
        ignoreCommand()
        directMessageSpyCommand()
    }
}