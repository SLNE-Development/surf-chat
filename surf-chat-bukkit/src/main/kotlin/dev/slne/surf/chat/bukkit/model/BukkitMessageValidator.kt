package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.MessageValidatorModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.filterService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class BukkitMessageValidator(): MessageValidatorModel {
    override fun validate(message: Component, type: ChatMessageType, sender: Player): MessageValidationResult {
        return filterService.find(message, sender)
    }

    override fun parse(message: Component, type: ChatMessageType, player: Player, onSuccess: () -> Unit) {
        when(plugin.messageValidator.validate(message, ChatMessageType.PRIVATE, player ?: return)) {
            MessageValidationResult.SUCCESS -> {
                onSuccess()
            }

            MessageValidationResult.FAILED_MUTED -> {
                surfChatApi.sendText(player, buildText {
                    error("Du bist stummgeschaltet.")
                })
            }

            MessageValidationResult.FAILED_SPAM -> {
                surfChatApi.sendText(player, buildText {
                    error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.")
                })
            }

            MessageValidationResult.FAILED_SELF -> {
                surfChatApi.sendText(player, buildText {
                    error("Du kannst dir nicht selbst eine Nachricht senden.")
                })
            }

            MessageValidationResult.FAILED_BAD_LINK -> {
                surfChatApi.sendText(player, buildText {
                    error("Bitte verschicke keine Links.")
                })
            }

            MessageValidationResult.FAILED_BAD_WORD -> {
                surfChatApi.sendText(player, buildText {
                    error("Bitte benutze keine verbotenen Wörter.")
                })
            }

            MessageValidationResult.FAILED_BAD_CHARACTER -> {
                surfChatApi.sendText(player, buildText {
                    error("Bitte benutze keine verbotenen Zeichen.")
                })
            }

            MessageValidationResult.FAILED_PM_DISABLED -> {
                surfChatApi.sendText(player, buildText {
                    error("Der Spieler hat Privatnachrichten deaktiviert.")
                })
            }

            MessageValidationResult.FAILED_BLACKLIST -> {
                surfChatApi.sendText(player, buildText {
                    error("Deine Nachricht enthält unerlaubte Wörter.")
                })
            }

            MessageValidationResult.FAILED_IGNORING -> {
                surfChatApi.sendText(player, buildText {
                    error("Du ignorierst den Spieler.")
                })
            }
        }
    }
}