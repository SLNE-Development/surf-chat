package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.MessageValidator
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.filterService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class BukkitMessageValidator() : MessageValidator {
    override fun validate(
        message: Component,
        type: MessageType,
        sender: Player
    ): MessageValidationResult {
        return filterService.find(message, sender)
    }

    override fun parse(
        message: Component,
        type: MessageType,
        player: Player,
        onSuccess: () -> Unit
    ) {
        when (plugin.messageValidator.validate(
            message,
            MessageType.PRIVATE,
            player
        )) {
            MessageValidationResult.SUCCESS -> {
                onSuccess()
            }

            MessageValidationResult.FAILED_MUTED -> {
                player.sendPrefixed {
                    error("Du bist stummgeschaltet.")
                }
            }

            MessageValidationResult.FAILED_SPAM -> {
                player.sendPrefixed {
                    error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.")
                }
            }

            MessageValidationResult.FAILED_SELF -> {
                player.sendPrefixed {
                    error("Du kannst dir nicht selbst eine Nachricht senden.")
                }
            }

            MessageValidationResult.FAILED_BAD_LINK -> {
                player.sendPrefixed {
                    error("Bitte verschicke keine Links.")
                }
            }

            MessageValidationResult.FAILED_BAD_WORD -> {
                player.sendPrefixed {
                    error("Bitte benutze keine verbotenen Wörter.")
                }
            }

            MessageValidationResult.FAILED_BAD_CHARACTER -> {
                player.sendPrefixed {
                    error("Bitte benutze keine verbotenen Zeichen.")
                }
            }

            MessageValidationResult.FAILED_PM_DISABLED -> {
                player.sendPrefixed {
                    error("Der Spieler hat Privatnachrichten deaktiviert.")
                }
            }

            MessageValidationResult.FAILED_DENYLISTED -> {
                player.sendPrefixed {
                    error("Deine Nachricht enthält unerlaubte Wörter.")
                }
            }

            MessageValidationResult.FAILED_IGNORING -> {
                player.sendPrefixed {
                    error("Du ignorierst den Spieler.")
                }
            }
        }
    }
}