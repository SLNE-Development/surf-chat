package dev.slne.surf.chat.api.functionality

import kotlinx.serialization.Serializable

@Serializable
data class Functionalities(
    val localChatEnabled: Boolean = true
) {
    companion object {
        val EMPTY = Functionalities()
    }
}
