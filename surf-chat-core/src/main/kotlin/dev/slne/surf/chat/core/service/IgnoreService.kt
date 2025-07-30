package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface IgnoreService : ServiceUsingDatabase {
    fun ignore(player: UUID, target: UUID)
    fun unignore(player: UUID, target: UUID)
    fun isIgnored(player: UUID, target: UUID): Boolean
    fun getIgnoreList(player: UUID): ObjectSet<UUID>

    companion object {
        val INSTANCE = requiredService<IgnoreService>()
    }
}

val ignoreService get() = IgnoreService.INSTANCE