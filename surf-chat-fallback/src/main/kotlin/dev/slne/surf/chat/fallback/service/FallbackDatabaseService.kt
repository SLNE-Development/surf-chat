package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChatUser
import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.core.service.DatabaseService
import dev.slne.surf.database.DatabaseProvider
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path
import java.util.UUID

@AutoService(DatabaseService::class)
class FallbackDatabaseService : DatabaseService, Services.Fallback {
    object ChatSettings : Table("chat_settings") {
        val userUuid = varchar("user_uuid", 36).transform(
            { UUID.fromString(it) },
            { it.toString() },
        ).uniqueIndex()
        val pingsEnabled = bool("pings_enabled").default(true)
        val privateMessagesEnabled = bool("private_messages_enabled").default(true)
        val privateMessageFriendBypass = bool("private_message_friend_bypass").default(true)
        val channelInvitesEnabled = bool("channel_invites_enabled").default(true)

        override val primaryKey = PrimaryKey(userUuid)
    }

    object ChatIgnorelist : Table("chat_ignorelist") {
        val userUuid = varchar("user_uuid", 36).transform(
            { UUID.fromString(it) },
            { it.toString() },
        )
        val ignoredUserUuid = varchar("ignored_user_uuid", 36).transform(
            { UUID.fromString(it) },
            { it.toString() },
        )
        override val primaryKey = PrimaryKey(userUuid)
    }

    object ChatDenylist : Table("chat_denylist") {
        val id = integer("id").autoIncrement().uniqueIndex()
        val word = text("word")
        val addedBy = varchar("added_by", 16)
        val reason = text("reason").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    object ChatHistory : Table("chat_history") {
        val entryUuid = varchar("entry_uuid", 36).transform(
            { UUID.fromString(it) },
            { it.toString() },
        ).uniqueIndex()
        val userUuid = varchar("user_uuid", 36).transform(
            { UUID.fromString(it) },
            { it.toString() },
        )
        val type = text("type")
        val message = text("message")
        val timestamp = long("timestamp")
        val server = text("server")
        val deletedBy = varchar("deleted_by", 16).nullable()

        override val primaryKey = PrimaryKey(entryUuid)
    }

    override fun connect(path: Path) {
        DatabaseProvider(path, path).connect()

        transaction {
            SchemaUtils.create(
                ChatSettings, ChatIgnorelist, ChatDenylist, ChatHistory
            )
        }
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