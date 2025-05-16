package dev.slne.surf.chat.bukkit.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.*
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.getUsername
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand

import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.format.TextDecoration
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.*

class IgnoreListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_IGNORE_LIST)
        integerArgument("page", 1, optional = true)

        playerExecutor { player, args ->
            val page = args.getOrDefaultUnchecked("page", 1)

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val ignores: ObjectSet<UUID> = user.ignoreList

                if (ignores.isEmpty()) {
                    user.sendPrefixed {
                        error("Du ignorierst niemanden.")
                    }
                    return@launch
                }

                val ignoredPlayers = ignores.map {
                    async {
                        it to it.getUsername()
                    }
                }.awaitAll()

                PageableMessageBuilder {
                    pageCommand = "/ignore #list %page%"

                    title {
                        info(("Ignorierte Spieler").toSmallCaps())
                    }

                    ignoredPlayers.forEach { (uuid, username) ->
                        line {
                            append {
                                info("| ")
                                decorate(TextDecoration.BOLD)
                            }
                            variableValue(username)

                            hoverEvent(
                                components.getIgnoreListHoverComponent(
                                    username
                                )
                            )
                            clickSuggestsCommand("/ignore $username")
                        }
                    }
                }.send(player, page)
            }
        }
    }
}