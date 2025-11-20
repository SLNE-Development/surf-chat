package dev.slne.surf.chat.core.common

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.ChatContextHolder
import dev.slne.surf.chat.api.InternalChatApi
import net.kyori.adventure.util.Services
import org.springframework.context.ApplicationContext

@OptIn(InternalChatApi::class)
@AutoService(ChatContextHolder::class)
class ChatContextHolderImpl : ChatContextHolder, Services.Fallback {
    override lateinit var context: ApplicationContext

    companion object {
        val instance = ChatContextHolder.instance as ChatContextHolderImpl
    }
}