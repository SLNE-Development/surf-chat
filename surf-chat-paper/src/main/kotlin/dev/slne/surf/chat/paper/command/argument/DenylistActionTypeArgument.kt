package dev.slne.surf.chat.paper.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class DenylistActionTypeArgument(nodeName: String) :
    CustomArgument<DenylistActionType, String>(StringArgument(nodeName), { info ->
        when (info.input()) {
            "kick" -> DenylistActionType.KICK
            "ban" -> DenylistActionType.EXPIRABLE_BAN
            "mute" -> DenylistActionType.MUTE
            "warn" -> DenylistActionType.WARN
            "communityban" -> DenylistActionType.COMMUNITY_BAN
            "ban-permanent" -> DenylistActionType.PERMANENT_BAN
            else -> throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Der Aktionstyp '${info.input()}' ist ungültig. Gültige Typen sind: kick, ban, mute, warn, communityban, ban-permanent.")
                }
            }
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.strings(
                "kick",
                "ban",
                "mute",
                "warn",
                "communityban",
                "ban-permanent"
            )
        )
    }
}

inline fun CommandAPICommand.denylistActionTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(DenylistActionTypeArgument(nodeName).setOptional(optional).apply(block))