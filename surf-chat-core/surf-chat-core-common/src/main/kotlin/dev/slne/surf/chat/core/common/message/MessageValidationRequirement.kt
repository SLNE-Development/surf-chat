package dev.slne.surf.chat.core.common.message

interface MessageValidationRequirement {
    val sendTeamWarning: Boolean
    fun test(messageData: MessageData): String?
}