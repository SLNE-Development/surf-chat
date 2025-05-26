package dev.slne.surf.chat.bukkit.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.google.auto.service.AutoService
import com.google.gson.reflect.TypeToken
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.bukkit.model.BukkitDenyListEntry
import dev.slne.surf.chat.bukkit.model.BukkitChatUser
import dev.slne.surf.chat.bukkit.model.BukkitHistoryEntry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.utils.gson
import dev.slne.surf.chat.core.service.DatabaseService
import dev.slne.surf.database.DatabaseProvider
import dev.slne.surf.surfapi.core.api.util.toMutableObjectSet
import dev.slne.surf.surfapi.core.api.util.toObjectList
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@AutoService(DatabaseService::class)
class BukkitDatabaseService() : DatabaseService, Fallback {
    private val loadHistoryMutex = Mutex()
    private val dataCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes)
        .withRemovalListener { uuid, data, _ ->
            if (uuid != null && data != null) {
                saveUser(data as ChatUser)
            }
        }
        .asLoadingCache<UUID, ChatUser> { loadUser(it) }

    private val nameCache: LoadingCache<UUID, String> = Caffeine.newBuilder()
        .expireAfterWrite(1.hours)
        .build { Bukkit.getOfflinePlayer(it).name ?: "Unknown" }

    object Users : Table() {
        val uuid = varchar("uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val ignoreList = text("ignoreList").transform(
            {
                val type = object : TypeToken<ObjectArraySet<UUID>>() {}.type
                gson.fromJson<ObjectArraySet<UUID>>(it, type).toMutableObjectSet()
            },
            {
                gson.toJson(it)
            }
        )

        val channelInvites = bool("channelInvites").default(true)
        val pmDisabled = bool("pmDisabled").default(false)
        val soundEnabled = bool("soundEnabled").default(true)

        override val primaryKey = PrimaryKey(uuid)
    }

    object ChatHistory : Table() {
        val entryUuid = varchar("id", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val userUuid = varchar("uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val type = text("type").transform({ MessageType.valueOf(it)}, { it.toString()} )
        val timeStamp = long("timeStamp")
        val message = text("message")
        val deletedBy = varchar("deletedBy", 16).nullable()
        val server = text("server")

        override val primaryKey = PrimaryKey(entryUuid)
    }

    object Denylist : Table() {
        val id = integer("id").autoIncrement()
        val word = text("word")
        val reason = text("reason")

        val addedAt = long("addedAt")
        val addedBy = varchar("addedBy", 16)

        override val primaryKey = PrimaryKey(id)
    }


    override fun connect() {
        DatabaseProvider(plugin.dataPath, plugin.dataPath).connect()

        transaction {
            SchemaUtils.create(Users, ChatHistory, Denylist)
        }
    }

    override suspend fun getUser(uuid: UUID): ChatUser {
        return dataCache.get(uuid)
    }

    override suspend fun loadUser(uuid: UUID): ChatUser {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val selected = Users.selectAll().where(Users.uuid eq uuid).firstOrNull()
                    ?: return@newSuspendedTransaction BukkitChatUser(uuid)

                return@newSuspendedTransaction selected.let {
                    BukkitChatUser(
                        uuid = it[Users.uuid],
                        ignoreList = it[Users.ignoreList],
                        pmDisabled = it[Users.pmDisabled],
                        soundEnabled = it[Users.soundEnabled],
                        channelInvites = it[Users.channelInvites]
                    )
                }
            }
        }
    }

    override suspend fun saveUser(user: ChatUser) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                Users.upsert {
                    it[uuid] = user.uuid
                    it[ignoreList] = user.ignoreList.toMutableObjectSet()
                    it[pmDisabled] = user.pmDisabled
                    it[soundEnabled] = user.soundEnabled
                    it[channelInvites] = user.channelInvites
                }
            }
        }
    }

    override suspend fun handleDisconnect(user: UUID) {
        dataCache.invalidate(user)
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
        deleted: Boolean?,
        deletedBy: String?,
        server: String?
    ): ObjectList<HistoryEntry> {
        return withContext(Dispatchers.IO) {
            withTimeout(10_000L) {
                loadHistoryMutex.withLock {
                    newSuspendedTransaction {
                        val now = System.currentTimeMillis()
                        val conditions = mutableListOf<Op<Boolean>>()

                        if (uuid != null) {
                            conditions += ChatHistory.userUuid eq uuid
                        }

                        if (type != null) {
                            conditions += ChatHistory.type eq MessageType.valueOf(type)
                        }

                        if (rangeMillis != null) {
                            val minTime = now - rangeMillis
                            conditions += ChatHistory.timeStamp greaterEq minTime
                        }

                        if (message != null) {
                            conditions += ChatHistory.message like "%$message%"
                        }

                        if (deletedBy != null) {
                            conditions += ChatHistory.deletedBy eq deletedBy
                        }

                        if (server != null) {
                            conditions += ChatHistory.server eq server
                        }

                        val query = if (conditions.isNotEmpty()) {
                            ChatHistory.selectAll()
                                .where(conditions.reduce { acc, condition -> acc and condition })
                        } else {
                            ChatHistory.selectAll()
                        }

                        return@newSuspendedTransaction query.map {
                            BukkitHistoryEntry(
                                entryUuid = it[ChatHistory.entryUuid],
                                userUuid = it[ChatHistory.userUuid],
                                type = it[ChatHistory.type],
                                timestamp = it[ChatHistory.timeStamp],
                                message = it[ChatHistory.message],
                                deletedBy = it[ChatHistory.deletedBy],
                                server = it[ChatHistory.server]
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
                val selected = Denylist.selectAll()

                return@newSuspendedTransaction selected.map {
                    BukkitDenyListEntry(
                        word = it[Denylist.word],
                        reason = it[Denylist.reason],
                        addedAt = it[Denylist.addedAt],
                        addedBy = it[Denylist.addedBy]
                    )
                }.toObjectSet()
            }

        }
    }

    override suspend fun addToDenylist(entry: DenyListEntry): Boolean {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                if (Denylist.selectAll().where(Denylist.word eq entry.word).empty()) {
                    Denylist.insert {
                        it[word] = entry.word
                        it[reason] = entry.reason
                        it[addedAt] = entry.addedAt
                        it[addedBy] = entry.addedBy
                    }
                    return@newSuspendedTransaction true
                } else {
                    return@newSuspendedTransaction false
                }
            }
        }
    }

    override suspend fun removeFromDenylist(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                if (Denylist.selectAll().where(Denylist.word eq word).empty()) {
                    return@newSuspendedTransaction false
                } else {
                    Denylist.deleteWhere { Denylist.word eq word }
                    return@newSuspendedTransaction true
                }
            }
        }
    }


    override suspend fun insertHistoryEntry(user: UUID, entry: HistoryEntry) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                ChatHistory.insert {
                    it[entryUuid] = entry.entryUuid
                    it[userUuid] = user
                    it[type] = entry.type
                    it[timeStamp] = entry.timestamp
                    it[message] = entry.message
                    it[deletedBy] = entry.deletedBy
                    it[server] = entry.server
                }
            }
        }
    }

    override suspend fun saveAll() {
        dataCache.asMap().forEach {
            saveUser(it.value)
        }
    }

    override fun getName(uuid: UUID): String {
        return nameCache.get(uuid)
    }
}