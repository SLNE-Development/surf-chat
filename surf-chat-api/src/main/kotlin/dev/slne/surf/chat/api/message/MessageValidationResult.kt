package dev.slne.surf.chat.api.message

import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component

sealed class MessageValidationResult {
    class Success : MessageValidationResult()
    data class Failure(val error: MessageValidationError) : MessageValidationResult()

    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure


    sealed class MessageValidationError(val errorMessage: Component) {
        class EmptyContent :
            MessageValidationError(buildText { error("Deine Nachricht darf nicht leer sein.") })

        data class DenylistedWord(val denylistEntry: DenylistEntry) :
            MessageValidationError(buildText { error("Bitte achte auf deine Wortwahl.") })

        data class BadLink(val url: String) :
            MessageValidationError(buildText { error("Deine Nachricht enthält einen unerlaubten Link.") })

        data class BadCharacters(val chars: String) :
            MessageValidationError(buildText { error("Deine Nachricht enthält unerlaubte Zeichen.") })

        data class TooOften(val next: Long) :
            MessageValidationError(buildText { error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.") })
    }
}