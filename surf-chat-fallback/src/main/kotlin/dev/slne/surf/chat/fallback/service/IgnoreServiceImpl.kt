package dev.slne.surf.chat.fallback.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.service.IgnoreService
import java.util.*

@AutoService(IgnoreService::class)
class IgnoreServiceImpl : IgnoreService {
    private val cache = Caffeine.newBuilder()
        .maximumSize(1000)
        .build<UUID, List<IgnoreListEntry>>()


    override suspend fun ignore(uuid: UUID, ignoredUUID: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unignore(uuid: UUID, ignoredUUID: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun isIgnored(uuid: UUID, ignoredUUID: UUID): Boolean {
        return cache.getIfPresent(uuid)?.any { it.target == ignoredUUID } ?: false
    }

    override fun getCachedIgnoreList(uuid: UUID): List<IgnoreListEntry> {
        return cache.getIfPresent(uuid) ?: emptyList()
    }

    override suspend fun loadIgnoreList(uuid: UUID): List<IgnoreListEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun cleanup(uuid: UUID) {
        cache.invalidate(uuid)
    }
}