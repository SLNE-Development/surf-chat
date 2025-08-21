package dev.slne.surf.chat.api.server

interface ChatServer {
  /**
   * The user-facing display name of the chat server.
   * This is intended to be shown to end users.
   */
  val name: String
  /**
   * The internal name of the chat server, used for identification within the system.
   *
   * Unlike [name], which is intended for display to end users and may be localized or formatted for presentation,
   * [internalName] is a stable, unique identifier used internally (e.g., for configuration, logging, or referencing servers in code).
   * Use [internalName] when you need a value that does not change and is not user-facing.
   */
  val internalName: String
}
