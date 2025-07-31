package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface IgnoreService : ServiceUsingDatabase {
    suspend fun ignore(player: UUID, playerName: String, target: UUID, targetPlayerName: String)
    suspend fun unIgnore(player: UUID, target: UUID)
    suspend fun isIgnored(player: UUID, target: UUID): Boolean
    suspend fun getIgnoreList(player: UUID): ObjectSet<IgnoreListEntry>

    companion object {
        val INSTANCE = requiredService<IgnoreService>()
    }
}

val ignoreService get() = IgnoreService.INSTANCE