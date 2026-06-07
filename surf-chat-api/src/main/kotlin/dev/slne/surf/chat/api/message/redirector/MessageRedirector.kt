package dev.slne.surf.chat.api.message.redirector

import dev.slne.surf.chat.api.message.MessageData
import net.kyori.adventure.chat.SignedMessage

interface MessageRedirector {
    suspend fun redirectMessage(signedMessage: SignedMessage, messageData: MessageData)
}