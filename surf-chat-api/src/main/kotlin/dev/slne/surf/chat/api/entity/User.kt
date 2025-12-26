package dev.slne.surf.chat.api.entity

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
/**
 * Represents a user in the chat system.
 *
 * @property name The name of the user.
 * @property uuid The unique identifier of the user.
 */
data class User(
    val name: String,
    val uuid: @Contextual UUID,

    var directMessagesEnabled: Boolean = true,
    var channelInviteMessagesEnabled: Boolean = true,
    var chatPingsEnabled: Boolean = true,

    val ignorelist: MutableList<IgnoreListEntry> = mutableListOf()
)