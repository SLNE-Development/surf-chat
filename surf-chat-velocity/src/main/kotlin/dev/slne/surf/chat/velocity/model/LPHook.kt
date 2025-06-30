package dev.slne.surf.chat.velocity.model

import com.velocitypowered.api.proxy.Player
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider

object LPHook {
    private var luckPermsApi: LuckPerms? = null

    fun initialize() {
        luckPermsApi = LuckPermsProvider.get()
    }

    fun getPrefix(player: Player): String {
        val luckperms = luckPermsApi ?: return "Internal LP Api error"
        return luckperms.getPlayerAdapter(Player::class.java)
            .getUser(player).cachedData.metaData.prefix ?: ""
    }
}