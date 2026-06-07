package dev.slne.surf.chat.api

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.redirector.MessageRedirector
import dev.slne.surf.chat.api.message.redirector.MessageRedirectorRegistry
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.api.processor.PreChatProcessor
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.TimeoutCancellationException
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

private val api = requiredService<SurfChatApi>()

interface SurfChatApi {
    suspend fun logMessage(data: MessageData)

    fun registerChatProcessor(processor: PreChatProcessor)
    fun registerChatProcessor(processor: PostChatProcessor)

    fun registerMessageRedirector(redirector: MessageRedirector) = MessageRedirectorRegistry.register(redirector)

    fun getCachedIgnoreList(uuid: UUID): List<IgnoreListEntry>
    suspend fun getIgnoreList(uuid: UUID): List<IgnoreListEntry>

    @Throws(TimeoutCancellationException::class)
    suspend fun lookupHistory(filter: HistoryFilter): ObjectList<HistoryEntry>

    suspend fun sendSignedMessage(
        signedMessage: SignedMessage,
        senderUuid: UUID,
        targetUuid: UUID,
        contentComponent: Component
    )


    suspend fun passAutoMod(messageData: MessageData)

    companion object : SurfChatApi by api
}