package dev.slne.surf.chat.paper.command.ignore

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.core.service.ignoreService
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.command.args.awaitingOrNull
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun ignoreCommand() = commandAPICommand("ignore", plugin) {
    withPermission(PermissionRegistry.COMMAND_IGNORE)
    ignoreListCommand()
    surfOfflinePlayerArgument("target")
    playerExecutorSuspend { player, args ->
        val target = args.awaitingOrNull<SurfPlayer?>("target")
            ?: throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.")

        if (player.uniqueId == target.uuid) {
            throw CommandAPI.failWithString("Du kannst dich nicht selbst ignorieren.")
        }

        if (ignoreService.unignore(player.uniqueId, target.uuid)) {
            player.sendText {
                appendSuccessPrefix()
                success("Du ignorierst nun nicht mehr ")
                variableValue(target.lastKnownName ?: target.uuid.toString())
                success(".")
            }
        } else {
            ignoreService.ignore(player.uniqueId, target.uuid)
            player.sendText {
                appendSuccessPrefix()
                success("Du ignorierst nun ")
                variableValue(target.lastKnownName ?: target.uuid.toString())
                success(".")
            }
        }
    }
}