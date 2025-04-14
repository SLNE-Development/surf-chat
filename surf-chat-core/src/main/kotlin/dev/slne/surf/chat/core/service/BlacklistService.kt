package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component

interface BlacklistService {
    fun isBlackListed(word: String): Boolean
    fun hasBlackListed(message: Component): Boolean

    fun addToBlacklist(name: String): Boolean
    fun removeFromBlacklist(name: String): Boolean

    companion object {
        val INSTANCE = requiredService<BlacklistService>()
    }
}

val blacklistService get() = BlacklistService.INSTANCE