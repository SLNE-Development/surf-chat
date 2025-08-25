package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.plugin

class ChatServerArgument(nodeName: String) :
    CustomArgument<ChatServer, String>(StringArgument(nodeName), { info ->
        ChatServer.of(info.input)
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            listOf(plugin.server.internalName)
        })
    }
}

inline fun CommandAPICommand.chatServerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(ChatServerArgument(nodeName).setOptional(optional).apply(block))