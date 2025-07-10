package dev.slne.surf.chat.fallback.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.core.service.DatabaseService
import dev.slne.surf.chat.fallback.model.entry.FallbackDenylistEntry
import dev.slne.surf.chat.fallback.model.entry.FallbackHistoryEntry
import dev.slne.surf.chat.fallback.model.user.FallbackChatUser
import dev.slne.surf.chat.fallback.model.user.FallbackChatUserSettings
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import dev.slne.surf.surfapi.core.api.util.toObjectList
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.nio.file.Path
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

@AutoService(DatabaseService::class)
class FallbackDatabaseService : DatabaseService, Services.Fallback {
    private val loadHistoryMutex = Mutex()
    private val userCache = Caffeine.newBuilder()
        .expireAfterWrite(30.minutes)
        .withRemovalListener { uuid, user, _ ->
            if (uuid != null && user != null) {
                this.saveUser(user as ChatUser)
            }
        }
        .asLoadingCache<UUID, ChatUser> { this.loadUser(it) }

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
        val addedAt = long("added_at")
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
        DatabaseManager(path, path).databaseProvider.connect()

        transaction {
            SchemaUtils.create(
                ChatSettings, ChatIgnorelist, ChatDenylist, ChatHistory
            )
        }
    }

    override suspend fun getUser(uuid: UUID): ChatUser {
        return userCache.get(uuid)
    }

    override suspend fun loadUser(uuid: UUID): ChatUser {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val settings = ChatSettings.selectAll().where(ChatSettings.userUuid eq uuid).map {
                    FallbackChatUserSettings(
                        pingsEnabled = it[ChatSettings.pingsEnabled],
                        pmEnabled = it[ChatSettings.privateMessagesEnabled],
                        pmFriendBypassEnabled = it[ChatSettings.privateMessageFriendBypass],
                        channelInvitesEnabled = it[ChatSettings.channelInvitesEnabled]
                    )
                }.firstOrNull() ?: FallbackChatUserSettings()

                val ignoreList = ChatIgnorelist.selectAll().where(ChatIgnorelist.userUuid eq uuid)
                    .map {
                        it[ChatIgnorelist.ignoredUserUuid]
                    }
                    .toObjectSet()

                FallbackChatUser (
                    uuid,
                    ignoreList,
                    settings,
                    PlayerLookupService.getUsername(uuid) ?: "Error"
                )
            }
        }
    }

    override suspend fun saveUser(user: ChatUser) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                ChatSettings.insertIgnore {
                    it[userUuid] = user.uuid
                    it[pingsEnabled] = user.settings.pingsEnabled
                    it[privateMessagesEnabled] = user.settings.pmEnabled
                    it[privateMessageFriendBypass] = user.settings.pmFriendBypassEnabled
                    it[channelInvitesEnabled] = user.settings.channelInvitesEnabled
                }

                ChatIgnorelist.deleteWhere { ChatIgnorelist.userUuid eq user.uuid }
                user.ignoreList.forEach { ignoredUser ->
                    ChatIgnorelist.insert {
                        it[userUuid] = user.uuid
                        it[ignoredUserUuid] = ignoredUser
                    }
                }
            }
        }
    }

    override suspend fun handleDisconnect(user: UUID) {

    }

    override suspend fun markMessageDeleted(deleter: String, messageID: UUID) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                ChatHistory.update({ ChatHistory.entryUuid eq messageID }) {
                    it[deletedBy] = deleter
                }
            }
        }
    }

    override suspend fun loadHistory(
        uuid: UUID?,
        type: String?,
        rangeMillis: Long?,
        message: String?,
        deletedBy: String?,
        server: String?,
        id: UUID?
    ): ObjectList<HistoryEntry> {
        return withContext(Dispatchers.IO) {
            withTimeout(10_000L) {
                loadHistoryMutex.withLock {
                    newSuspendedTransaction {
                        val now = System.currentTimeMillis()

                        ChatHistory.selectAll().where (
                            Op.build {
                                listOfNotNull (
                                    uuid?.let { ChatHistory.userUuid eq it },
                                    type?.let { ChatHistory.type eq it },
                                    rangeMillis?.let { ChatHistory.timestamp greaterEq now - it },
                                    message?.let { ChatHistory.message like "%$it%" },
                                    deletedBy?.let { ChatHistory.deletedBy eq it },
                                    server?.let { ChatHistory.server eq it },
                                    id?.let { ChatHistory.entryUuid eq it }
                                ).reduceOrNull(Op<Boolean>::and) ?: Op.TRUE
                            }
                        ).mapNotNull {
                            val entryUuid = it[ChatHistory.entryUuid]
                            val userUuid = it[ChatHistory.userUuid]
                            val messageText = it[ChatHistory.message]
                            val timestamp = it[ChatHistory.timestamp]
                            val serverName = it[ChatHistory.server]
                            val typeValue = it[ChatHistory.type]
                            val deletedByValue = it[ChatHistory.deletedBy]

                            FallbackHistoryEntry(
                                entryUuid,
                                userUuid,
                                MessageType.valueOf(typeValue),
                                timestamp,
                                messageText,
                                deletedByValue,
                                serverName
                            )
                        }.toObjectList()
                    }
                }
            }
        }
    }


    override suspend fun loadDenyList(): ObjectSet<DenyListEntry> {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                ChatDenylist.selectAll().map {
                    FallbackDenylistEntry (
                        it[ChatDenylist.word],
                        it[ChatDenylist.reason],
                        it[ChatDenylist.addedAt],
                        it[ChatDenylist.addedBy]
                    )
                }.toObjectSet()
            }
        }
    }

    override suspend fun addToDenylist(entry: DenyListEntry): Boolean {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val existing = ChatDenylist.select(ChatDenylist.word eq entry.word).firstOrNull()
                if (existing != null) {
                    false
                } else {
                    ChatDenylist.insert {
                        it[word] = entry.word
                        it[reason] = entry.reason
                        it[addedAt] = entry.addedAt
                        it[addedBy] = entry.addedBy
                    }
                    true
                }
            }
        }
    }

    override suspend fun removeFromDenylist(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val deletedRows = ChatDenylist.deleteWhere { ChatDenylist.word eq word }
                deletedRows > 0
            }
        }
    }

    override suspend fun insertHistoryEntry(
        user: UUID,
        entry: HistoryEntry
    ) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                ChatHistory.insertIgnore {
                    it[entryUuid] = entry.entryUuid
                    it[userUuid] = user
                    it[type] = entry.type.name
                    it[message] = entry.message
                    it[timestamp] = entry.timestamp
                    it[server] = entry.server
                    it[deletedBy] = entry.deletedBy
                }
            }
        }
    }

    override suspend fun saveAll() {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                userCache.invalidateAll()
            }
        }
    }
}