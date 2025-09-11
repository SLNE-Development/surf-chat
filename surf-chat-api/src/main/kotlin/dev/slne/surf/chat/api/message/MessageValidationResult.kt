package dev.slne.surf.chat.api.message

import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component

sealed class MessageValidationResult {
    class Success : MessageValidationResult()
    data class Failure(val error: MessageValidationError) : MessageValidationResult()

    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure

    fun getErrorOrNull(): MessageValidationError? = (this as? Failure)?.error
    fun getErrorOrThrow(): MessageValidationError =
        (this as? Failure)?.error ?: error("No error found.")

    sealed class MessageValidationError(val errorMessage: Component, val name: String) {
        class EmptyContent :
            MessageValidationError(
                buildText { error("Deine Nachricht darf nicht leer sein.") },
                "Kein Inhalt"
            )

        data class DenylistedWord(val denylistEntry: DenylistEntry) :
            MessageValidationError(
                buildText { error("Bitte achte auf deine Wortwahl.") },
                "Unerlaubtes Wort: ${denylistEntry.word}"
            )

        data class BadLink(val url: String) :
            MessageValidationError(
                buildText { error("Deine Nachricht enthält einen unerlaubten Link.") },
                "Unerlaubter Link: $url"
            )

        data class BadCharacters(val chars: String) :
            MessageValidationError(
                buildText { error("Deine Nachricht enthält unerlaubte Zeichen.") },
                "Unerlaubte Zeichen: $chars"
            )

        data class TooOften(val next: Long) :
            MessageValidationError(
                buildText { error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.") },
                "Spam"
            )

        class AutoDisabled :
            MessageValidationError(
                buildText { error("Du kannst zurzeit nicht schreiben.") },
                "Zu viele Spieler"
            )

        class ChatDisabled :
            MessageValidationError(
                buildText { error("Der Chat ist vorübergehend deaktiviert.") },
                "Chat deaktiviert"
            )
    }
}