package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.MessageValidatorModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.toPlayer
import dev.slne.surf.chat.core.service.filterService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class BukkitMessageValidator(): MessageValidatorModel {
    override fun validate(message: Component, type: ChatMessageType, sender: Player): MessageValidationResult {
        return filterService.find(message, sender)
    }

    override fun parse(message: Component, type: ChatMessageType, player: ChatUserModel, onSuccess: () -> Unit) {
        when(plugin.messageValidator.validate(message, ChatMessageType.PRIVATE_GENERAL, player.toPlayer() ?: return)) {
            MessageValidationResult.SUCCESS -> {
                onSuccess()
            }

            MessageValidationResult.FAILED_MUTED -> {
                player.sendText(buildText {
                    error("Du bist gemutet.")//TODO: Proper mute message
                })
            }

            MessageValidationResult.FAILED_SPAM -> {
                player.sendText(buildText {
                    error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.")
                })
            }

            MessageValidationResult.FAILED_SELF -> {
                player.sendText(buildText {
                    error("Du kannst dir nicht selbst eine Nachricht senden.")
                })
            }

            MessageValidationResult.FAILED_BAD_LINK -> {
                player.sendText(buildText {
                    error("Bitte verschicke keine Links.")
                })
            }

            MessageValidationResult.FAILED_BAD_WORD -> {
                player.sendText(buildText {
                    error("Bitte benutze keine verbotenen Wörter.")
                })
            }

            MessageValidationResult.FAILED_BAD_CHARACTER -> {
                player.sendText(buildText {
                    error("Bitte benutze keine verbotenen Zeichen.")
                })
            }

            MessageValidationResult.FAILED_PM_DISABLED -> {
                player.sendText(buildText {
                    error("Der Spieler hat Privatnachrichten deaktiviert.")
                })
            }

            MessageValidationResult.FAILED_BLACKLIST -> {
                player.sendText(buildText {
                    error("Bitte achte auf deine Wortwahl.")
                })
            }

            MessageValidationResult.FAILED_IGNORING -> {
                player.sendText(buildText {
                    error("Du ignorierst den Spieler.")
                })
            }
        }
    }
}