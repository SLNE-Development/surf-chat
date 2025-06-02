package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.jetbrains.annotations.Blocking
import java.nio.file.Path
import java.util.*

interface DatabaseService {
    @Blocking
    fun connect(path: Path)

    suspend fun getUser(uuid: UUID): ChatUser
    suspend fun loadUser(uuid: UUID): ChatUser
    suspend fun saveUser(user: ChatUser)
    suspend fun handleDisconnect(user: UUID)
    suspend fun markMessageDeleted(deleter: String, messageID: UUID)
    suspend fun loadHistory(
        uuid: UUID? = null,
        type: String? = null,
        rangeMillis: Long? = null,
        message: String? = null,
        deletedBy: String? = null,
        server: String? = null,
        id: UUID? = null,
    ): ObjectList<HistoryEntry>

    suspend fun loadDenyList(): ObjectSet<DenyListEntry>
    suspend fun addToDenylist(entry: DenyListEntry): Boolean
    suspend fun removeFromDenylist(word: String): Boolean

    suspend fun insertHistoryEntry(user: UUID, entry: HistoryEntry)

    suspend fun saveAll()

    companion object {
        val INSTANCE = requiredService<DatabaseService>()
    }
}

val databaseService get() = DatabaseService.INSTANCE