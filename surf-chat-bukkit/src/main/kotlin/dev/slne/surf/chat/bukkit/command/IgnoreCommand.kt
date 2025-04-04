package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.plugin
import dev.slne.surf.social.chat.util.Components
import org.bukkit.OfflinePlayer

class IgnoreCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.ignore")
        offlinePlayerArgument("target")

        playerExecutor { player, args ->
            val target: OfflinePlayer by args
            val targetUuid = target.uniqueId

            plugin.launch {
                val user = ChatUser.getUser(player.uniqueId)

                if (targetUuid == player.uniqueId) {
                    SurfChat.send(player, Components.cannotIgnoreSelf)
                    return@launch
                }

                if (user.isIgnoring(targetUuid)) {
                    user.ignoreList.remove(targetUuid)
                    SurfChat.send(player, Components.getIgnoreComponent(target, false))
                } else {
                    user.ignoreList.add(targetUuid)
                    SurfChat.send(player, Components.getIgnoreComponent(target, true))
                }
            }
        }
    }
}
