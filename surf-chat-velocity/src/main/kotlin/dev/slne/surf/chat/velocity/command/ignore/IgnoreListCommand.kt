package dev.slne.surf.chat.velocity.command.ignore

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.PageableMessageBuilder
import dev.slne.surf.chat.velocity.util.components
import dev.slne.surf.chat.velocity.util.getUsername
import dev.slne.surf.chat.velocity.util.sendText
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

            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val ignores: ObjectSet<UUID> = user.ignoreList

                if (ignores.isEmpty()) {
                    user.sendText {
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