package dev.slne.surf.chat.fallback.repository

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.fallback.table.IgnoreListTable
import dev.slne.surf.chat.fallback.table.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

val userRepository = UserRepository()

class UserRepository {
    suspend fun loadUserByUuid(uuid: UUID): User? = newSuspendedTransaction(Dispatchers.IO) {
        UserTable.selectAll().where(UserTable.uuid eq uuid).firstOrNull()?.let { userRow ->
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
                this.ignorelist.addAll(ignorelist)
            }
        }
    }

    suspend fun loadUserByName(name: String): User? = newSuspendedTransaction(Dispatchers.IO) {
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
                this.ignorelist.addAll(ignorelist)
            }
        }
    }

    suspend fun saveUser(user: User) = newSuspendedTransaction(Dispatchers.IO) {
        UserTable.upsert {
            it[name] = user.name
            it[uuid] = user.uuid
            it[directMessagesEnabled] = user.directMessagesEnabled
            it[channelInviteMessagesEnabled] = user.channelInviteMessagesEnabled
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
            channelInviteMessagesEnabled = row[UserTable.channelInviteMessagesEnabled],
            chatPingsEnabled = row[UserTable.chatPingsEnabled]
        )
    }
}