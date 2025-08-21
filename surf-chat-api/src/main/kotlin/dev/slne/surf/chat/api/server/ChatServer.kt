package dev.slne.surf.chat.api.server

interface ChatServer {
  /**
   * The user-facing display name of the chat server.
   * This is intended to be shown to end users.
   */
  val name: String
  val internalName: String
}
