package dev.slne.surf.chat.microservice.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.common.service.IgnoreService
import java.time.OffsetDateTime
import java.util.*

@AutoService(IgnoreService::class)
class IgnoreServiceImpl : IgnoreService {
    private val cache = Caffeine.newBuilder()
        .maximumSize(1000)
        .build<UUID, List<IgnoreListEntry>>()

    override suspend fun ignore(uuid: UUID, ignoredUUID: UUID): Boolean {
        val ignored = IgnoreRepository.ignore(uuid, ignoredUUID)
        if (ignored) {
            cache.asMap().compute(uuid) { _, ignoreList ->
                if (ignoreList == null) {
                    listOf(IgnoreListEntry(uuid, ignoredUUID, OffsetDateTime.now()))
                } else {
                    ignoreList + IgnoreListEntry(uuid, ignoredUUID, OffsetDateTime.now())
                }
            }
        }

        return ignored
    }

    override suspend fun unignore(uuid: UUID, ignoredUUID: UUID): Boolean {
        val unignored = IgnoreRepository.unignore(uuid, ignoredUUID)
        if (unignored) {
            cache.asMap().compute(uuid) { _, ignoreList ->
                ignoreList?.filterNot { it.target == ignoredUUID }
            }
        }

        return unignored
    }

    override fun isIgnored(uuid: UUID, ignoredUUID: UUID): Boolean {
        return cache.getIfPresent(uuid)?.any { it.target == ignoredUUID } ?: false
    }

    override fun getCachedIgnoreList(uuid: UUID): List<IgnoreListEntry> {
        return cache.getIfPresent(uuid) ?: emptyList()
    }

    override suspend fun loadIgnoreList(uuid: UUID): List<IgnoreListEntry> {
        val ignoreList = IgnoreRepository.findAllByUuid(uuid)
        cache.put(uuid, ignoreList)
        return ignoreList
    }

    override suspend fun cleanup(uuid: UUID) {
        cache.invalidate(uuid)
    }
}