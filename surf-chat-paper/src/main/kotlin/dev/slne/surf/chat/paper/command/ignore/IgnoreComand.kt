package dev.slne.surf.chat.paper.command.ignore

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.api.core.command.args.awaitingOrNull
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument

fun ignoreCommand() = commandAPICommand("ignore") {
    withPermission(PermissionRegistry.COMMAND_IGNORE)
    ignoreListCommand()
    surfOfflinePlayerArgument("target")
    playerExecutorSuspend { player, args ->
        val target = args.awaitingOrNull<SurfPlayer?>("target") ?: run {
            player.sendText {
                appendErrorPrefix()
                error("Der Spieler wurde nicht gefunden.")
            }
            return@playerExecutorSuspend
        }

        if (player.uniqueId == target.uuid) {
            player.sendText {
                appendErrorPrefix()
                error("Du kannst dich nicht selbst ignorieren.")
            }
            return@playerExecutorSuspend
        }

        if (IgnoreService.unignore(player.uniqueId, target.uuid)) {
            player.sendText {
                appendSuccessPrefix()
                success("Du ignorierst nun nicht mehr ")
                variableValue(target.username)
                success(".")
            }
        } else {
            IgnoreService.ignore(player.uniqueId, target.uuid)
            player.sendText {
                appendSuccessPrefix()
                success("Du ignorierst nun ")
                variableValue(target.username)
                success(".")
            }
        }
    }
}