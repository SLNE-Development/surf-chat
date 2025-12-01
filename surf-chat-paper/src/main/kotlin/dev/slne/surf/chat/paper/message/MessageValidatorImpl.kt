package dev.slne.surf.chat.paper.message

import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.chat.core.client.denylist.denylistService
import dev.slne.surf.chat.core.client.functionality.functionalityService
import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.chat.core.common.message.MessageValidationRequirement
import dev.slne.surf.chat.core.common.message.MessageValidator
import dev.slne.surf.chat.core.common.netty.packet.serverbound.ServerboundDenylistActionPacket
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.hasPlatformPermission
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import org.bukkit.Bukkit
import org.springframework.beans.factory.annotation.Autowired

class MessageValidatorImpl {
    companion object {
        fun validator(messageData: MessageData): MessageValidator {
            return StringMessageValidator(messageData)
        }
    }

    private class StringMessageValidator(
        override val messageData: MessageData
    ) : MessageValidator {
        @Autowired
        lateinit var requirements: List<MessageValidationRequirement>

        override fun validate(user: CloudPlayer): MessageValidationResult {
            val message = messageData.plainMessage
            if (user.hasPlatformPermission(SurfChatPermissionRegistry.TEAM_BYPASS_FILTER)) {
                return MessageValidationResult.Success
            }

            if (this.checkAutoDisabling(user)) {
                return MessageValidationResult.Failure("Der Chat ist zurzeit deaktiviert, da sich zu viele Spieler auf dem Server befinden.")
            }

            if (!functionalityService.isLocalChatEnabled() && !user.hasPlatformPermission(
                    SurfChatPermissionRegistry.TEAM_BYPASS_FUNCTIONALITY
                )
            ) {
                return MessageValidationResult.Failure("Du kannst zurzeit nicht schreiben.")
            }

            if (message.isBlank()) {
                return MessageValidationResult.Failure("Deine Nachricht darf nicht leer sein!")
            }

            denylistService.getEntries().find { message.contains(it.word, true) }
                ?.let { entry ->
                    ServerboundDenylistActionPacket(
                        messageData.messageUuid,
                        entry,
                        messageData.signature,
                        messageData.sender
                    ).fireAndForget()
                    
                    return MessageValidationResult.Failure(
                        "Bitte achte auf deine Wortwahl!"
                    )
                }

            val failure = requirements.firstNotNullOfOrNull {
                it.test(messageData)
            }

            failure?.let {
                return MessageValidationResult.Failure(failure)
            }

            return MessageValidationResult.Success
        }


        fun checkAutoDisabling(player: CloudPlayer): Boolean =
            !player.hasPlatformPermission(SurfChatPermissionRegistry.AUTO_CHAT_DISABLING_BYPASS) && Bukkit.getOnlinePlayers()
                .count() > (getMinAmountForServer() ?: 0) && isDisablingEnabled()

        fun isDisablingEnabled() =
            SyncValues.autoDisablingMinAmounts.any { it.serverPattern.matches(CloudServer.current().name) }

        fun getMinAmountForServer(): Int? =
            SyncValues.autoDisablingMinAmounts.firstOrNull { it.serverPattern.matches(CloudServer.current().name) }?.value
    }
}