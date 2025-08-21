package dev.slne.surf.chat.api.server

import it.unimi.dsi.fastutil.objects.ObjectSet

interface ChatGroup {
  val name: String
  val members: ObjectSet<ChatServer>
}
