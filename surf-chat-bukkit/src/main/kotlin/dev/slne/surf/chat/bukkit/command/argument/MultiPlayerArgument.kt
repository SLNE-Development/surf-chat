package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class MultiPlayerArgument(nodeName: String) : CustomArgument<ObjectSet<Player>, String>(
    GreedyStringArgument(nodeName), { info ->
        val input = info.input.trim()

        if (input.isEmpty()) {
            throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Du musst mindestens einen Spieler angeben.")
                }
            }
        }

        val returnedPlayers: ObjectSet<Player> = mutableObjectSetOf()

        if (input.equals("all", ignoreCase = true)) {
            returnedPlayers.addAll(Bukkit.getOnlinePlayers())
        } else {
            val names = input.split("\\s+".toRegex())
            val players = names.mapNotNull { Bukkit.getPlayerExact(it) }.toObjectSet()

            if (players.size != names.size) {
                throw CustomArgumentException.fromAdventureComponent {
                    buildText {
                        appendPrefix()
                        error("Ein oder mehrere Spieler konnten nicht gefunden werden.")
                    }
                }
            }

            returnedPlayers.addAll(players)
        }

        returnedPlayers
    }
) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            listOf("all") + Bukkit.getOnlinePlayers().map { it.name }
        })
    }
}

inline fun CommandAPICommand.multiPlayerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(MultiPlayerArgument(nodeName).setOptional(optional).apply(block))
