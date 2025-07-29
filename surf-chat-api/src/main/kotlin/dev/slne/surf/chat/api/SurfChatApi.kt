package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

interface SurfChatApi {
    suspend fun logMessage(
        message: Component,
        type: MessageType,
        sender: User,
        receiver: User? = null,
        sentAt: Long = System.currentTimeMillis(),
        server: String = "unspecified",
        channel: Channel? = null,
        signedMessage: SignedMessage? = null
    )

    fun getUser(name: String): User?
    fun getUser(uuid: UUID): User?
    fun createUser(name: String, uuid: UUID): User

    suspend fun lookupHistory(filter: HistoryFilter): ObjectSet<HistoryEntry>

    fun createChannel(name: String, owner: User): Channel
    fun deleteChannel(channel: Channel)
    fun getChannel(name: String): Channel?
    fun getChannels(): ObjectSet<Channel>

    fun invite(channel: Channel, user: User): Boolean
    fun uninvite(channel: Channel, user: User): Boolean
    fun isInvited(channel: Channel, user: User): Boolean

    fun acceptInvite(channel: Channel, user: User): Boolean
    fun declineInvite(channel: Channel, user: User): Boolean

    companion object {
        val INSTANCE = requiredService<SurfChatApi>()
    }
}

val surfChatApi get() = SurfChatApi.INSTANCE