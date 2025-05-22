package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChatUser
import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.core.service.DatabaseService
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.UUID

@AutoService(DatabaseService::class)
class FallbackDatabaseService : DatabaseService, Services.Fallback {
    override fun connect() {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(uuid: UUID): ChatUser {
        TODO("Not yet implemented")
    }

    override suspend fun loadUser(uuid: UUID): ChatUser {
        TODO("Not yet implemented")
    }

    override suspend fun saveUser(user: ChatUser) {
        TODO("Not yet implemented")
    }

    override suspend fun handleDisconnect(user: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun markMessageDeleted(deleter: String, messageID: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun loadHistory(
        uuid: UUID?,
        type: String?,
        rangeMillis: Long?,
        message: String?,
        deleted: Boolean?,
        deletedBy: String?,
        server: String?
    ): ObjectList<HistoryEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun loadDenyList(): ObjectSet<DenyListEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun addToDenylist(entry: DenyListEntry): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromDenylist(word: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insertHistoryEntry(
        user: UUID,
        entry: HistoryEntry
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun saveAll() {
        TODO("Not yet implemented")
    }

    override fun getName(uuid: UUID): String {
        TODO("Not yet implemented")
    }
}