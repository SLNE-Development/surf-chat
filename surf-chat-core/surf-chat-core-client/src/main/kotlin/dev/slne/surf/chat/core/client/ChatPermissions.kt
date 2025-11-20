package dev.slne.surf.chat.core.client

object ChatPermissions {
    private const val BASE = "surf.chat"
    private const val COMMAND = "$BASE.command"

    const val COMMAND_PRIVATE = "$COMMAND.whisper"
    const val COMMAND_REPLY = "$COMMAND.reply"
}