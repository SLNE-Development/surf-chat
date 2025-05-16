package dev.slne.surf.chat.bukkit.command

import dev.jorel.commandapi.CommandAPI
import dev.slne.surf.chat.bukkit.command.channel.ChannelCommand
import dev.slne.surf.chat.bukkit.command.denylist.DenyListCommand
import dev.slne.surf.chat.bukkit.command.ignore.IgnoreCommand
import dev.slne.surf.chat.bukkit.command.ignore.IgnoreListCommand
import dev.slne.surf.chat.bukkit.command.surfchat.SurfChatCommand
import dev.slne.surf.chat.bukkit.command.toggle.ToggleCommand

object CommandManager {
    fun registerAll() {
        CommandAPI.unregister("msg")
        CommandAPI.unregister("tell")
        CommandAPI.unregister("w")

        ChannelCommand("channel").register()
        SurfChatCommand("surfchat").register()
        IgnoreCommand("ignore").register()
        PrivateMessageCommand("msg").register()
        ReplyCommand("reply").register()
        ToggleCommand("toggle").register()
        TeamChatCommand("teamchat").register()
        DenyListCommand("denylist").register()
        PrivateMessageSpyCommand("pmspy").register()
    }
}