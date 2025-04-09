package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.gradle.internal.impldep.org.bouncycastle.util.Integers
import java.util.UUID

interface DatabaseService {
    fun connect()

    suspend fun getUser(uuid: UUID): ChatUserModel
    suspend fun loadUser(uuid: UUID): ChatUserModel
    suspend fun saveUser(user: ChatUserModel)
    suspend fun handleDisconnect(user: UUID)
    suspend fun markMessageDeleted(deleter: String, messageID: UUID)
    suspend fun loadHistory(uuid: UUID? = null, type: String? = null, rangeMillis: Long? = null, message: String? = null, deleted: Boolean? = null, deletedBy: String? = null): ObjectList<HistoryEntryModel>

    suspend fun insertHistoryEntry(user: UUID, entry: HistoryEntryModel)

    suspend fun getName(uuid: UUID): String

    companion object {
        val INSTANCE = requiredService<DatabaseService>()
    }
}

val databaseService get() = DatabaseService.INSTANCE