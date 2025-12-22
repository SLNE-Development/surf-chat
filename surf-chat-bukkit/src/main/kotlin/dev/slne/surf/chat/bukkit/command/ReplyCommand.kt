package dev.slne.surf.chat.bukkit.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument


fun replyCommand() = commandAPICommand("reply") {
    withAliases("r")
    withPermission("surf.chat.command.reply")
    greedyStringArgument("message")


}