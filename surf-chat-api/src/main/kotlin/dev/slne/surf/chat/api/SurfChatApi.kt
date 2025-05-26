package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import java.util.*

interface SurfChatApi {
    /**
     * Logs a message for a specific player.
     * After logging, the message can be deleted by chat interaction or command.
     *
     * @param player The UUID of the player.
     * @param type The type of chat message.
     * @param message The message to log.
     * @param messageID The unique identifier for the logged message.
     */
    suspend fun logMessage(player: UUID, type: MessageType, message: Component, messageID: UUID, server: String)

    /**
     * Creates a new chat channel with the specified name and owner.
     *
     * @param name The name of the channel to create.
     * @param owner The owner of the channel, represented as a `ChatUser`.
     */
    fun createChannel(name: String, owner: ChatUser)

    /**
     * Deletes an existing chat channel by its name.
     *
     * @param name The name of the channel to delete.
     */
    fun deleteChannel(name: String)

    /**
     * Retrieves a chat channel by its name.
     *
     * @param name The name of the channel to retrieve.
     * @return The `Channel` object if found, or `null` if no channel with the given name exists.
     */
    fun getChannel(name: String): Channel?

    companion object {
        val INSTANCE = requiredService<SurfChatApi>()
    }
}

val surfChatApi get() = SurfChatApi.INSTANCE