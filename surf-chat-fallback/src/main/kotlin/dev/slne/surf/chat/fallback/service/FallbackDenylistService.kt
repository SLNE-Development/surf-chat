package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.core.service.DenylistService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services

@AutoService(DenylistService::class)
class FallbackDenylistService : DenylistService, Services.Fallback {
    override fun isDenylisted(word: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasDenyListed(message: Component): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addToDenylist(word: DenyListEntry): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromDenylist(word: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun fetch() {
        TODO("Not yet implemented")
    }
}