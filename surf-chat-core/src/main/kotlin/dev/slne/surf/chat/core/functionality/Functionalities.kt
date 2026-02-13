package dev.slne.surf.chat.core.functionality

data class Functionalities(
    val localChatEnabled: Boolean = true
) {
    companion object {
        val EMPTY = Functionalities()
    }
}
