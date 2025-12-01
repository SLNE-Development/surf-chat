package dev.slne.surf.chat.core.common.message

import dev.slne.surf.chat.api.message.MessageData

interface MessageValidationRequirement {
    val sendTeamWarning: Boolean
    fun test(messageData: MessageData): String?
}