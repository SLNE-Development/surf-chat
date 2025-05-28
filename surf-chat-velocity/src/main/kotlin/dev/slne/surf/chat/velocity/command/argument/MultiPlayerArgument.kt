package dev.slne.surf.chat.velocity.command.argument

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.velocitypowered.api.command.VelocityBrigadierMessage
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CommandAPIArgumentType
import dev.jorel.commandapi.executors.CommandArguments
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlin.jvm.optionals.getOrNull

@Suppress("UNCHECKED_CAST")
class MultiPlayerArgument(nodeName: String) : Argument<ObjectSet<Player>>(nodeName, StringArgumentType.string()) {
    override fun getPrimitiveType(): Class<ObjectSet<Player>> {
        return ObjectSet::class.java as Class<ObjectSet<Player>>
    }

    override fun getArgumentType(): CommandAPIArgumentType {
        return CommandAPIArgumentType.PRIMITIVE_TEXT
    }

    override fun <Source : Any> parseArgument(
        cmdCtx: CommandContext<Source>,
        key: String,
        previousArgs: CommandArguments
    ): ObjectSet<Player> {
        val players = mutableObjectSetOf<Player>()
        val argument = StringArgumentType.getString(cmdCtx, key)

        if(argument == "--all") {
            players.addAll(plugin.proxy.allPlayers)
            return players
        }

        players.add(plugin.proxy.getPlayer(StringArgumentType.getString(cmdCtx, key)).getOrNull() ?: throw SimpleCommandExceptionType(
            VelocityBrigadierMessage.tooltip(buildText {
                appendPrefix()
                error("Der Spieler wurde nicht gefunden.")
            })
        ).create())

        return players
    }

    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            plugin.proxy.allPlayers.map { it.username }.also { "--all" }
        })
    }
}

inline fun CommandAPICommand.multiPlayerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(
    MultiPlayerArgument(nodeName).setOptional(optional).apply(block)
)