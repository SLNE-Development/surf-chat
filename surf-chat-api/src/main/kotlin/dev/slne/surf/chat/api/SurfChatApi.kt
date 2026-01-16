package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

/**
 * API for managing chat functionality in the system.
 */
interface SurfChatApi {

    /**
     * Logs a message in the chat system.
     *
     * @param message The content of the message.
     * @param type The type of the message (e.g., global, etc.).
     * @param sender The user who sent the message.
     * @param receiver The user who received the message, or `null` if not applicable.
     * @param sentAt The timestamp (in milliseconds since epoch) when the message was sent. Defaults to the current time.
     * @param server The server where the message was sent. Defaults to "unspecified".
     * @param signedMessage The signed message object, or `null` if not applicable.
     */
    suspend fun logMessage(
        message: Component,
        type: MessageType,
        sender: User,
        receiver: User? = null,
        sentAt: Long = System.currentTimeMillis(),
        server: String = "unspecified",
        signedMessage: SignedMessage? = null,
        messageUuid: UUID = UUID.randomUUID()
    )

    /**
     * Retrieves a user by their name.
     *
     * @param name The name of the user.
     * @return The user object, or `null` if not found.
     */
    fun getUser(name: String): User?

    /**
     * Retrieves a user by their UUID.
     *
     * @param uuid The UUID of the user.
     * @return The user object, or `null` if not found.
     */
    fun getUser(uuid: UUID): User?


    /**
     * Looks up chat history based on a filter.
     *
     * @param filter The filter criteria for querying the history.
     * @return A set of history entries matching the filter.
     */
    suspend fun lookupHistory(filter: HistoryFilter): ObjectSet<HistoryEntry>

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