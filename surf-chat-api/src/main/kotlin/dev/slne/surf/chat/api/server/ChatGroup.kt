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
  val members: ObjectSet<ChatServer>
}
