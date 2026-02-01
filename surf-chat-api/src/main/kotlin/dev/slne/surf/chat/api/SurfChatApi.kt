package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.TimeoutCancellationException
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

/**
 * API for managing chat functionality in the system.
 */
interface SurfChatApi {

    /**
     * Logs a message in the chat system.
     */
    suspend fun logMessage(data: MessageData)

    fun getCachedIgnoreList(uuid: UUID): List<IgnoreListEntry>
    suspend fun getIgnoreList(uuid: UUID): List<IgnoreListEntry>

    /**
     * Looks up chat history based on a filter.
     *
     * @param filter The filter criteria for querying the history.
     * @return A set of history entries matching the filter.
     */
    @Throws(TimeoutCancellationException::class)
    suspend fun lookupHistory(filter: HistoryFilter): ObjectList<HistoryEntry>

    companion object {
        /**
         * The singleton instance of the `SurfChatApi`.
         */
        val INSTANCE = requiredService<SurfChatApi>()
    }
}

/**
 * Provides access to the singleton instance of the `SurfChatApi`.
 */
val surfChatApi get() = SurfChatApi.INSTANCE