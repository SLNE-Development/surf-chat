package dev.slne.surf.chat.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import org.springframework.context.ApplicationContext

@InternalChatApi
interface ChatContextHolder {
    val context: ApplicationContext

    companion object {
        val instance = requiredService<ChatContextHolder>()
    }
}