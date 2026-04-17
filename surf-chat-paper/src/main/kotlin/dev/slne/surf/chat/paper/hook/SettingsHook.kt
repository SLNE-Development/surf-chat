package dev.slne.surf.chat.paper.hook

import dev.slne.surf.settings.api.SurfSettingsApi
import dev.slne.surf.settings.api.setting.SettingKeys
import java.util.*

object SettingsHook {
    fun hasDirectMessagesEnabled(playerUuid: UUID): Boolean =
        SurfSettingsApi.getSettingValue(playerUuid, SettingKeys.DIRECT_MESSAGES)

    fun hasChatPingsEnabled(playerUuid: UUID): Boolean =
        SurfSettingsApi.getSettingValue(playerUuid, SettingKeys.CHAT_PINGS)
}