package dev.slne.surf.chat.bukkit.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.*
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

class IgnoreListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_IGNORE_LIST)
        integerArgument("page", 1, Int.MAX_VALUE, true)

        playerExecutor { player, args ->
            val page = args.getOrDefaultUnchecked("page", 1)

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val ignores: ObjectSet<UUID> = user.ignoreList

                if (ignores.isEmpty()) {
                    user.sendText(
                        error("Du ignorierst niemanden.")
                    )
                    return@launch
                }

                val ignoredPlayers = ignores.map { entry ->
                    entry to entry.getUsername()
                }

                PageableMessageBuilder {
                    pageCommand = "/ignore list %page%"

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

                            hoverEvent(HoverEvent.showText {
                                components.getIgnoreListHoverComponent(
                                    username
                                )
                            })
                            clickSuggestsCommand("/ignore $username")
                        }
                    }

                }.send(player, page)
            }
        }
    }
}