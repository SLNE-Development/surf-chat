package dev.slne.surf.chat.api.server

data class ChatServer(
    /**
     * The user-facing display name of the chat server.
     * This is intended to be shown to end users.
     */
    val name: String,

    /**
     * The internal name of the chat server, used for identification within the system.
     *
     * Unlike [name], which is intended for display to end users and may be localized or formatted for presentation,
     * [internalName] is a stable, unique identifier used internally (e.g., for configuration, logging, or referencing servers in code).
     * Use [internalName] when you need a value that does not change and is not user-facing.
     */
    val internalName: String
) {


    companion object {
        fun default() = ChatServer(
            name = "Default",
            internalName = "default"
        )

        fun of(internalName: String) = ChatServer(
            name = internalName.lowercase().replaceFirstChar { it.uppercase() },
            internalName = internalName
        )

        fun of(name: String, internalName: String) = ChatServer(
            name = name,
            internalName = internalName
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatServer) return false

        if (internalName != other.internalName) return false

        return true
    }

    override fun hashCode(): Int {
        return internalName.hashCode()
    }
}
