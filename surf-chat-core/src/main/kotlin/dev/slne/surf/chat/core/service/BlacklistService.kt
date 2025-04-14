package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface BlacklistService {
    fun isBlackListed(name: String): Boolean

    fun addToBlacklist(name: String): Boolean
    fun removeFromBlacklist(name: String): Boolean

    companion object {
        val INSTANCE = requiredService<BlacklistService>()
    }
}

val blacklistService get() = BlacklistService.INSTANCE