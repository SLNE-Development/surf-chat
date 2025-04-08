package dev.slne.surf.chat.bukkit.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.bukkit.gson
import dev.slne.surf.chat.bukkit.model.BukkitChatUser
import dev.slne.surf.chat.bukkit.model.BukkitHistoryEntry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.DatabaseService
import dev.slne.surf.database.DatabaseProvider
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.util.Services.Fallback
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
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

    object Users : Table() {
        val uuid = varchar("uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val name = text("name")
        val ignoreList = text("ignoreList").transform({ gson.fromJson(it, ObjectArraySet<UUID>().javaClass) }, { gson.toJson(it) })
        val pmToggled = bool("pmToggled")

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


    override fun connect() {
        DatabaseProvider(plugin.dataPath, plugin.dataPath).connect()

        transaction {
            SchemaUtils.create(Users, ChatHistory)
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
                        name = it[Users.name],
                        ignoreList = it[Users.ignoreList],
                        pmToggled = it[Users.pmToggled]
                    )
                }
            }
        }
    }

    override suspend fun saveUser(user: ChatUserModel) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                Users.replace {
                    it[uuid] = user.uuid
                    it[name] = user.name
                    it[ignoreList] = user.ignoreList
                    it[pmToggled] = user.pmToggled
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

    override suspend fun loadHistory(uuid: UUID): ObjectList<HistoryEntryModel> {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val selected = ChatHistory.selectAll().where(ChatHistory.uuid eq uuid)

                return@newSuspendedTransaction selected.map {
                    BukkitHistoryEntry(
                        id = it[ChatHistory.id],
                        uuid = it[ChatHistory.uuid],
                        type = it[ChatHistory.type],
                        timestamp = it[ChatHistory.timeStamp],
                        message = it[ChatHistory.message],
                        deleted = it[ChatHistory.deleted],
                        deletedBy = it[ChatHistory.deletedBy],
                        )
                }
            }.toObjectList()
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
}