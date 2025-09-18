package dev.slne.surf.chat.api.server

import it.unimi.dsi.fastutil.objects.ObjectSet

/**
 * Represents a group chat consisting of multiple chat servers.
 */
interface ChatGroup {
    /**
     * The name of the chat group.
     *
     * Represents the display name of the group, typically used for identifying the chat group within interfaces
     * or user-facing components.
     */
    val name: String

    /**
     * A set of chat servers that are members of the chat group.
     *
     * This collection contains all the servers associated with the group, each represented as a [ChatServer] instance.
     * It may be used for operations such as querying, managing, or broadcasting messages to all member servers within the group.
     */
    val members: ObjectSet<ChatServer>
}
