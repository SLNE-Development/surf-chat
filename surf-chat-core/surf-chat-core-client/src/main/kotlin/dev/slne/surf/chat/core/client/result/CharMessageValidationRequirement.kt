package dev.slne.surf.chat.core.client.result

import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.chat.core.common.message.MessageValidationRequirement
import org.springframework.stereotype.Component

@Component
class CharMessageValidationRequirement : MessageValidationRequirement {
    private val validCharactersRegex =
        "^[\\u0020-\\u007EäöüÄÖÜß€@£¥|²³µ½¼¾«»¡¿°§´`^~¨]+$".toRegex()
    override val sendTeamWarning = false

    override fun test(messageData: MessageData) =
        if (messageData.plainMessage.none { validCharactersRegex.matches(it.toString()) }) null else "Deine Nachricht enthält unerlaubte Zeichen."
}