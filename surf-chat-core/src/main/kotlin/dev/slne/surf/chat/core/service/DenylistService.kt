package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component

interface DenylistService {
    fun isDenylisted(word: String): Boolean
    fun hasDenyListed(message: Component): Boolean

    suspend fun addToDenylist(word: String, reason: String, addedAt: Long, addedBy: String): Boolean
    suspend fun removeFromDenylist(word: String): Boolean

    suspend fun fetch()

    companion object {
        val INSTANCE = requiredService<DenylistService>()
    }
}

val denylistService get() = DenylistService.INSTANCE