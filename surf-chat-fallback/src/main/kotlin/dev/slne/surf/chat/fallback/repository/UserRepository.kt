package dev.slne.surf.chat.fallback.repository

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.fallback.table.IgnoreListTable
import dev.slne.surf.chat.fallback.table.UserTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.*

val userRepository = UserRepository()

class UserRepository {
    suspend fun loadUserByUuid(uuid: UUID): User? = suspendTransaction {
        UserTable.selectAll().where(UserTable.uuid eq uuid).firstOrNull()?.let { userRow ->
            toUser(userRow).apply {
                this.ignorelist.addAll(
                    loadIgnorelistById(
                        userRow[UserTable.id].value,
                        userRow[UserTable.uuid],
                        userRow[UserTable.name]
                    )
                )
            }
        }
    }

    suspend fun loadUserByName(name: String): User? = suspendTransaction {
        UserTable.selectAll().where(UserTable.name eq name).firstOrNull()?.let { userRow ->
            val ignorelist = IgnoreListTable.selectAll()
                .where(IgnoreListTable.userId eq userRow[UserTable.id].value).map { ignoreListRow ->
                    IgnoreListEntry(
                        userRow[UserTable.uuid],
                        userRow[UserTable.name],
                        ignoreListRow[IgnoreListTable.targetUuid],
                        ignoreListRow[IgnoreListTable.targetName],
                        ignoreListRow[IgnoreListTable.createdAt]
                    )
                }
            toUser(userRow).apply {
                this.ignorelist.addAll(ignorelist.toList())
            }
        }
    }

    suspend fun loadUserOrCreateByUuid(uuid: UUID, name: String): User = suspendTransaction {
        UserTable.selectAll().where(UserTable.uuid eq uuid).firstOrNull()?.let {
            toUser(it)
        } ?: run {
            UserTable.insert {
                it[this.uuid] = uuid
                it[this.name] = name
                it[this.directMessagesEnabled] = true
                it[this.chatPingsEnabled] = true
            }
            User(
                name = name,
                uuid = uuid,
                directMessagesEnabled = true,
                chatPingsEnabled = true
            )
        }
    }

    suspend fun saveUser(user: User) = suspendTransaction {
        UserTable.upsert {
            it[name] = user.name
            it[uuid] = user.uuid
            it[directMessagesEnabled] = user.directMessagesEnabled
            it[chatPingsEnabled] = user.chatPingsEnabled
        }

        val userId =
            UserTable.selectAll().where(UserTable.uuid eq user.uuid).first()[UserTable.id].value
        IgnoreListTable.deleteWhere { IgnoreListTable.userId eq userId }
        user.ignorelist.forEach { entry ->
            IgnoreListTable.insert {
                it[this.userId] = userId
                it[targetUuid] = entry.target
                it[targetName] = entry.targetName
                it[createdAt] = entry.createdAt
            }
        }
    }

    private fun toUser(row: ResultRow): User {
        return User(
            name = row[UserTable.name],
            uuid = row[UserTable.uuid],
            directMessagesEnabled = row[UserTable.directMessagesEnabled],
            chatPingsEnabled = row[UserTable.chatPingsEnabled]
        )
    }

    private suspend fun loadIgnorelistById(
        id: Long,
        uuid: UUID,
        name: String
    ): List<IgnoreListEntry> = suspendTransaction {
        IgnoreListTable.selectAll()
            .where(IgnoreListTable.userId eq id).map { ignoreListRow ->
                IgnoreListEntry(
                    uuid,
                    name,
                    ignoreListRow[IgnoreListTable.targetUuid],
                    ignoreListRow[IgnoreListTable.targetName],
                    ignoreListRow[IgnoreListTable.createdAt]
                )
            }.toList()
    }
}