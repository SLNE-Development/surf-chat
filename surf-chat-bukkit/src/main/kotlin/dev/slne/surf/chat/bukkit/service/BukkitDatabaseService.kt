package dev.slne.surf.chat.bukkit.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.google.auto.service.AutoService
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.chat.api.model.BlacklistWordModel
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.bukkit.gson
import dev.slne.surf.chat.bukkit.model.BukkitBlacklistWord
import dev.slne.surf.chat.bukkit.model.BukkitChatUser
import dev.slne.surf.chat.bukkit.model.BukkitHistoryEntry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.DatabaseService
import dev.slne.surf.database.DatabaseProvider
import dev.slne.surf.surfapi.core.api.util.toObjectList
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@AutoService(DatabaseService::class)
class BukkitDatabaseService(): DatabaseService, Fallback {
    private val dataCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes)
        .withRemovalListener { uuid, data, _ ->
            if (uuid != null && data != null) {
                saveUser(data as ChatUserModel)
            }
        }
        .asLoadingCache<UUID, ChatUserModel> { loadUser(it) }

    private val nameCache: LoadingCache<UUID, String> = Caffeine.newBuilder()
        .expireAfterWrite(1.hours)
        .build{ Bukkit.getOfflinePlayer(it).name ?: "Unknown" }

    object Users : Table() {
        val uuid = varchar("uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val ignoreList = text("ignoreList").transform({ gson.fromJson(it, ObjectArraySet<UUID>().javaClass) }, { gson.toJson(it) })
        val pmToggled = bool("pmToggled").default(false)
        val likesSound = bool("likesSound").default(true)

        override val primaryKey = PrimaryKey(uuid)
    }

    object ChatHistory : Table() {
        val id = varchar("id", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val uuid = varchar("uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val type = text("type")
        val timeStamp = long("timeStamp")
        val message = text("message")
        val deleted = bool("deleted").default(false)
        val deletedBy = varchar("deletedBy", 16)

        override val primaryKey = PrimaryKey(id)
    }

    object BlackList : Table() {
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
            SchemaUtils.create(Users, ChatHistory, BlackList)
        }
    }

    override suspend fun getUser(uuid: UUID): ChatUserModel {
        return dataCache.get(uuid)
    }

    override suspend fun loadUser(uuid: UUID): ChatUserModel {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val selected = Users.selectAll().where(Users.uuid eq uuid).firstOrNull() ?: return@newSuspendedTransaction BukkitChatUser(uuid)

                return@newSuspendedTransaction selected.let {
                    BukkitChatUser(
                        uuid = it[Users.uuid],
                        ignoreList = it[Users.ignoreList],
                        pmToggled = it[Users.pmToggled],
                        likesSound = it[Users.likesSound]
                    )
                }
            }
        }
    }

    override suspend fun saveUser(user: ChatUserModel) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                Users.upsert {
                    it[uuid] = user.uuid
                    it[ignoreList] = user.ignoreList
                    it[pmToggled] = user.pmToggled
                    it[likesSound] = user.likesSound
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
                ChatHistory.update({ ChatHistory.id eq messageID }) {

                    it[deleted] = true
                    it[deletedBy] = deleter
                }
            }
        }
    }

    override suspend fun loadHistory (
        uuid: UUID?,
        type: String?,
        rangeMillis: Long?,
        message: String?,
        deleted: Boolean?,
        deletedBy: String?
    ): ObjectList<HistoryEntryModel> {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val now = System.currentTimeMillis()
                val conditions = mutableListOf<Op<Boolean>>()

                if (uuid != null) {
                    conditions += ChatHistory.uuid eq uuid
                }

                if (type != null) {
                    conditions += ChatHistory.type eq type
                }

                if (rangeMillis != null) {
                    val minTime = now - rangeMillis
                    conditions += ChatHistory.timeStamp greaterEq minTime
                }

                if (message != null) {
                    conditions += ChatHistory.message like "%$message%"
                }

                if (deleted != null) {
                    conditions += ChatHistory.deleted eq deleted
                }

                if (deletedBy != null) {
                    conditions += ChatHistory.deletedBy eq deletedBy
                }

                val query = if (conditions.isNotEmpty()) {
                    ChatHistory.selectAll().where (conditions.reduce { acc, condition -> acc and condition })
                } else {
                    ChatHistory.selectAll()
                }


                return@newSuspendedTransaction query.map {
                    BukkitHistoryEntry(
                        id = it[ChatHistory.id],
                        uuid = it[ChatHistory.uuid],
                        type = it[ChatHistory.type],
                        timestamp = it[ChatHistory.timeStamp],
                        message = it[ChatHistory.message],
                        deleted = it[ChatHistory.deleted],
                        deletedBy = it[ChatHistory.deletedBy]
                    )
                }.toObjectList()
            }
        }
    }

    override suspend fun loadBlacklist(): ObjectSet<BlacklistWordModel> {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val selected = BlackList.selectAll()

                return@newSuspendedTransaction selected.map {
                    BukkitBlacklistWord (
                        word = it[BlackList.word],
                        reason = it[BlackList.reason],
                        addedAt = it[BlackList.addedAt],
                        addedBy = it[BlackList.addedBy]
                    )
                }.toObjectSet()
            }

        }
    }

    override suspend fun addToBlacklist(entry: BlacklistWordModel): Boolean {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                if(BlackList.selectAll().where (BlackList.word eq entry.word).empty()) {
                    BlackList.insert {
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

    override suspend fun removeFromBlacklist(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                if (BlackList.selectAll().where (BlackList.word eq word).empty()) {
                    return@newSuspendedTransaction false
                } else {
                    BlackList.deleteWhere { BlackList.word eq word }
                    return@newSuspendedTransaction true
                }
            }
        }
    }


    override suspend fun insertHistoryEntry(user: UUID, entry: HistoryEntryModel) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                ChatHistory.insert {
                    it[id] = entry.id
                    it[uuid] = user
                    it[type] = entry.type
                    it[timeStamp] = entry.timestamp
                    it[message] = entry.message
                    it[deleted] = entry.deleted
                    it[deletedBy] = entry.deletedBy
                }
            }
        }
    }

    override fun saveAll() {
        dataCache.invalidateAll()
    }

    override fun getName(uuid: UUID): String {
        return nameCache.get(uuid)
    }
}