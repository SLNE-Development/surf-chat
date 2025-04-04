package dev.slne.surf.chat.bukkit.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.retrooper.packetevents.protocol.player.User
import com.google.auto.service.AutoService
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.bukkit.gson
import dev.slne.surf.chat.bukkit.model.BukkitChatUser
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.DatabaseService
import dev.slne.surf.database.DatabaseProvider
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.util.Services.Fallback
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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
        val uuid = text("uuid").transform({ UUID.fromString(it) }, { it.toString() })
        val name = text("name")
        val ignoreList = text("ignoreList").transform( {
            gson.fromJson(it, ObjectArraySet<UUID>().toObjectSet().javaClass)
        }, {
            gson.toJson(it)
        })

        val pmToggled = bool("pmToggled")

        override val primaryKey = PrimaryKey(uuid)
    }

    override fun connect() {
        DatabaseProvider(plugin.dataPath, plugin.dataPath).connect()
    }

    override suspend fun getUser(uuid: UUID): ChatUserModel {
        return dataCache.get(uuid)
    }

    override suspend fun loadUser(uuid: UUID): ChatUserModel {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val selected = Users.select(Users.uuid eq uuid).firstOrNull() ?: return@newSuspendedTransaction BukkitChatUser(uuid)

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
}