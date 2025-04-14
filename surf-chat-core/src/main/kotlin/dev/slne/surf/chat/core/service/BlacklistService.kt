package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.BlacklistWordModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component

interface BlacklistService {
    fun isBlackListed(word: String): Boolean
    fun hasBlackListed(message: Component): Boolean

    suspend fun addToBlacklist(word: BlacklistWordModel): Boolean
    suspend fun removeFromBlacklist(word: String): Boolean

    suspend fun fetch()

    companion object {
        val INSTANCE = requiredService<BlacklistService>()
    }
}

val blacklistService get() = BlacklistService.INSTANCE