package dev.slne.surf.chat.minestom

import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.core.client.processor.post.AiModerationPostChatProcessor
import dev.slne.surf.chat.core.client.processor.post.LogPostChatProcessor
import dev.slne.surf.chat.core.client.processor.post.PrivateMessageSpyPostChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.IgnorePreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.SlowChatPreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.ValidatorPreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.validate.CharPreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.validate.LinkPreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.validate.SpamPreChatProcessor
import dev.slne.surf.chat.minestom.listener.ChatEventListener
import dev.slne.surf.chat.minestom.processor.pre.CorrectViewersPreChatProcessor
import dev.slne.surf.chat.minestom.processor.pre.FormatPreChatProcessor

/**
 * Registers the chat processors that make up the chat pipeline of this platform.
 */
fun registerProcessors() {
    chatProcessorRegistry.register(CharPreChatProcessor)
    chatProcessorRegistry.register(LinkPreChatProcessor)
    chatProcessorRegistry.register(SpamPreChatProcessor)
    chatProcessorRegistry.register(CorrectViewersPreChatProcessor)
    chatProcessorRegistry.register(FormatPreChatProcessor)
    chatProcessorRegistry.register(IgnorePreChatProcessor)
    chatProcessorRegistry.register(ValidatorPreChatProcessor)
    chatProcessorRegistry.register(SlowChatPreChatProcessor)

    chatProcessorRegistry.register(LogPostChatProcessor)
    chatProcessorRegistry.register(AiModerationPostChatProcessor)
    chatProcessorRegistry.register(PrivateMessageSpyPostChatProcessor)
}

/**
 * Registers the listener that runs incoming chat messages through the chat pipeline.
 */
fun registerChatListener() {
    ChatEventListener.register()
}
