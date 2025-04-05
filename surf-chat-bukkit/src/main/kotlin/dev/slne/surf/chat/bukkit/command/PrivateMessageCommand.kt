package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.type.MessageValidationError
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.toDisplayUser
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerArgument("player")
        greedyStringArgument("message")

        withAliases("tell", "w", "pm", "dm")
        withPermission("surf.chat.command.private-message")
        playerExecutor {player, args ->
            plugin.launch {
                val target = args.getUnchecked<Player>("player") ?: return@launch
                val message = args.getUnchecked<String>("message") ?: return@launch
                val messageComponent = Component.text(message)

                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                when(plugin.messageValidator.validate(messageComponent, ChatMessageType.PRIVATE_GENERAL)) {
                    MessageValidationError.SUCCESS -> {
                        targetUser.sendText(plugin.chatFormat.formatMessage(messageComponent, player.toDisplayUser(), target.toDisplayUser(), ChatMessageType.PRIVATE_TO, ""))
                    }

                    MessageValidationError.FAILED_MUTED -> {
                        user.sendText(buildText {
                            error("Du bist gemutet.")//TODO: Proper mute message
                        })
                    }

                    MessageValidationError.FAILED_SPAM -> {
                        user.sendText(buildText {
                            error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.")
                        })
                    }

                    MessageValidationError.FAILED_SELF -> {
                        user.sendText(buildText {
                            error("Du kannst dir nicht selbst eine Nachricht senden.")
                        })
                    }

                    MessageValidationError.FAILED_BAD_LINK -> {
                        user.sendText(buildText {
                            error("Bitte verschicke keine Links.")
                        })
                    }

                    MessageValidationError.FAILED_BAD_WORD -> {
                        user.sendText(buildText {
                            error("Bitte benutze keine verbotenen Wörter.")
                        })
                    }

                    MessageValidationError.FAILED_BAD_CHARACTER -> {
                        user.sendText(buildText {
                            error("Bitte benutze keine verbotenen Zeichen.")
                        })
                    }

                    MessageValidationError.FAILED_PM_DISABLED -> {
                        user.sendText(buildText {
                            error("Der Spieler hat Privatnachrichten deaktiviert.")
                        })
                    }

                    MessageValidationError.FAILED_BLACKLIST -> {
                        user.sendText(buildText {
                            error("Bitte achte auf deine Wortwahl.")
                        })
                    }

                    MessageValidationError.FAILED_IGNORING -> {
                        user.sendText(buildText {
                            error("Du ignorierst den Spieler.")
                        })
                    }
                }
            }
        }
    }
}
