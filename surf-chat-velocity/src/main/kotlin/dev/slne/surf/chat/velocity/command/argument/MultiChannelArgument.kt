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
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet

@Suppress("UNCHECKED_CAST")
class MultiChannelArgument(nodeName: String) : Argument<ObjectSet<Channel>>(nodeName, StringArgumentType.string()) {
    override fun getPrimitiveType(): Class<ObjectSet<Channel>> {
        return ObjectSet::class.java as Class<ObjectSet<Channel>>
    }

    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection { _ ->
            channelService.getAllChannels().map { it.name }.toList().also { "--all" }
        })
    }

    override fun getArgumentType(): CommandAPIArgumentType {
        return CommandAPIArgumentType.PRIMITIVE_TEXT
    }

    override fun <Source : Any> parseArgument(
        cmdCtx: CommandContext<Source>,
        key: String,
        previousArgs: CommandArguments
    ): ObjectSet<Channel> {
        val argument = StringArgumentType.getString(cmdCtx, key)

        if(argument == "--all") {
            return channelService.getAllChannels()
        }

        val channel = channelService.getChannel(argument)
        return if (channel != null) {
            mutableObjectSetOf(channel)
        } else {
            throw SimpleCommandExceptionType(
                VelocityBrigadierMessage.tooltip(buildText {
                    appendPrefix()
                    error("Der Kanal wurde nicht gefunden.")
                })
            ).create()
        }
    }
}

inline fun CommandAPICommand.multiChannelArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(
    MultiChannelArgument(nodeName).setOptional(optional).apply(block)
)