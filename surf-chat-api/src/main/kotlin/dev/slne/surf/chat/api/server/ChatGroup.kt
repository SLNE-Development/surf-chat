package dev.slne.surf.chat.api.server

import it.unimi.dsi.fastutil.objects.ObjectSet

interface ChatGroup {
  /**
   * The name of the chat group.
   *
   * This property represents the display name of the chat group, which is used to identify the group
   * within the chat system. It may be shown to users and should be unique within the context where
   * chat groups are managed.
   */
  val name: String
  /**
   * The set of members in this chat group.
   *
   * This set is read-only (immutable) from the perspective of the interface consumer.
   * Implementations may return a mutable or immutable set, but consumers should not modify it.
   *
   * There are no explicit minimum or maximum size constraints; the set may be empty.
   */
  val members: ObjectSet<ChatServer>
}
