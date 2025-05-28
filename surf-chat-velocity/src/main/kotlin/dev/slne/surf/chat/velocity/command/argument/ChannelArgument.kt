package dev.slne.surf.chat.velocity.command.argument

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.velocitypowered.api.command.VelocityBrigadierMessage
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CommandAPIArgumentType
import dev.jorel.commandapi.executors.CommandArguments

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelArgument(nodeName: String) : Argument<Channel>(nodeName, StringArgumentType.string()) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection { _ ->
            channelService.getAllChannels().map { it.name }
        })
    }

    override fun getPrimitiveType(): Class<Channel> {
        return Channel::class.java
    }

    override fun getArgumentType(): CommandAPIArgumentType {
        return CommandAPIArgumentType.PRIMITIVE_TEXT
    }

    override fun <Source : Any> parseArgument(
        ctx: CommandContext<Source>,
        key: String,
        previousArgs: CommandArguments
    ): Channel {
        return channelService.getChannel(StringArgumentType.getString(ctx, key)) ?: throw SimpleCommandExceptionType (
            VelocityBrigadierMessage.tooltip(buildText {
                appendPrefix()
                error("Der Kanal wurde nicht gefunden.")
            })
        ).create()
    }
}

inline fun CommandAPICommand.channelArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(
    ChannelArgument(nodeName).setOptional(optional).apply(block)
)