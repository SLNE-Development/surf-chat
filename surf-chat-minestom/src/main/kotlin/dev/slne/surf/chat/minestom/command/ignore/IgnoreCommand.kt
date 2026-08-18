package dev.slne.surf.chat.minestom.command.ignore

import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandTree
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.minestom.command.argument.surfOfflinePlayerArgument
import kotlinx.coroutines.Deferred

fun ignoreCommand() = commandTree("ignore") {
    withPermission(ChatPermissions.COMMAND_IGNORE)
    ignoreListCommand()
    surfOfflinePlayerArgument("target") {
        playerExecutorSuspend { player, args ->
            val target = args.get<Deferred<SurfPlayer?>>("target").await() ?: run {
                player.sendText {
                    appendErrorPrefix()
                    error("Der Spieler wurde nicht gefunden.")
                }
                return@playerExecutorSuspend
            }

            if (player.uuid == target.uuid) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du kannst dich nicht selbst ignorieren.")
                }
                return@playerExecutorSuspend
            }

            if (IgnoreService.unignore(player.uuid, target.uuid)) {
                player.sendText {
                    appendSuccessPrefix()
                    success("Du ignorierst nun nicht mehr ")
                    variableValue(target.username)
                    success(".")
                }
            } else {
                IgnoreService.ignore(player.uuid, target.uuid)
                player.sendText {
                    appendSuccessPrefix()
                    success("Du ignorierst nun ")
                    variableValue(target.username)
                    success(".")
                }
            }
        }
    }
}
