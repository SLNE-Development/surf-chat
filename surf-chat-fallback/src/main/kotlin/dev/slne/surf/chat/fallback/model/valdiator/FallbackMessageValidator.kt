package dev.slne.surf.chat.fallback.model.valdiator

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.model.MessageValidator
import dev.slne.surf.chat.api.model.messageValidator
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.core.service.filterService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services

@AutoService(MessageValidator::class)
class FallbackMessageValidator : MessageValidator, Services.Fallback {
    override fun validate(
        message: Component,
        type: MessageType,
        user: ChatUser
    ): MessageValidationResult {
        return filterService.find(message, user)
    }

    override fun parse(
        message: Component,
        type: MessageType,
        user: ChatUser,
        onSuccess: () -> Unit
    ) {
        when (messageValidator.validate(
            message,
            type,
            user
        )) {
            MessageValidationResult.SUCCESS -> {
                onSuccess()
            }

            MessageValidationResult.FAILED_MUTED -> {
                user.sendText {
                    error("Du bist stummgeschaltet.")
                }
            }

            MessageValidationResult.FAILED_SPAM -> {
                user.sendText {
                    error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.")
                }
            }

            MessageValidationResult.FAILED_SELF -> {
                user.sendText {
                    error("Du kannst dir nicht selbst eine Nachricht senden.")
                }
            }

            MessageValidationResult.FAILED_BAD_LINK -> {
                user.sendText {
                    error("Bitte verschicke keine Links.")
                }
            }

            MessageValidationResult.FAILED_BAD_WORD -> {
                user.sendText {
                    error("Bitte benutze keine verbotenen Wörter.")
                }
            }

            MessageValidationResult.FAILED_BAD_CHARACTER -> {
                user.sendText {
                    error("Bitte benutze keine verbotenen Zeichen.")
                }
            }

            MessageValidationResult.FAILED_PM_DISABLED -> {
                user.sendText {
                    error("Der Spieler hat Privatnachrichten deaktiviert.")
                }
            }

            MessageValidationResult.FAILED_DENYLISTED -> {
                user.sendText {
                    error("Deine Nachricht enthält unerlaubte Wörter.")
                }
            }

            MessageValidationResult.FAILED_IGNORING -> {
                user.sendText {
                    error("Du ignorierst den Spieler.")
                }
            }
        }
    }
}