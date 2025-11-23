package dev.slne.surf.chat.paper.hook

import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager
import net.luckperms.api.LuckPermsProvider
import java.util.*

object LuckPermsHook {
    fun isEnabled() = pluginManager.isPluginEnabled("LuckPerms")

    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun getPrefix(player: UUID) =
        if (isEnabled()) luckPerms.userManager.getUser(player)?.cachedData?.metaData?.prefix
            ?: "" else ""

    fun getSuffix(player: UUID) =
        if (isEnabled()) luckPerms.userManager.getUser(player)?.cachedData?.metaData?.suffix
            ?: "" else ""
}