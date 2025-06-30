package dev.slne.surf.chat.bukkit.extension

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bukkit.entity.Player

object LPHook {
    private var luckPermsApi: LuckPerms? = null

    fun initialize() {
        luckPermsApi = LuckPermsProvider.get()
    }

    fun getPrefix(player: Player): String {
        val luckperms = luckPermsApi ?: return "Internal Api error"
        return luckperms.getPlayerAdapter(Player::class.java)
            .getUser(player).cachedData.metaData.prefix ?: ""
    }
}