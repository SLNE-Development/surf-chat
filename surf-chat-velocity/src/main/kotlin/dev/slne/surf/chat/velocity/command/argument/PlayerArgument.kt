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
import kotlin.jvm.optionals.getOrNull

class PlayerArgument(nodeName: String) : Argument<Player>(nodeName, StringArgumentType.string()) {
    override fun getPrimitiveType(): Class<Player> {
        return Player::class.java
    }

    override fun getArgumentType(): CommandAPIArgumentType {
        return CommandAPIArgumentType.PRIMITIVE_TEXT
    }

    override fun <Source : Any> parseArgument(
        cmdCtx: CommandContext<Source>,
        key: String,
        previousArgs: CommandArguments
    ): Player {
        return plugin.proxy.getPlayer(StringArgumentType.getString(cmdCtx, key)).getOrNull()
            ?: throw SimpleCommandExceptionType(
                VelocityBrigadierMessage.tooltip(buildText {
                    appendPrefix()
                    error("Der Spieler wurde nicht gefunden.")
                })
            ).create()
    }

    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            plugin.proxy.allPlayers.map { it.username }
        })
    }
}

inline fun CommandAPICommand.playerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(
    PlayerArgument(nodeName).setOptional(optional).apply(block)
)