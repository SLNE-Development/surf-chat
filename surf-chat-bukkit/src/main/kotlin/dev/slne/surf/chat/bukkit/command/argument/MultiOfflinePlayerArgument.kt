package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.*
import dev.slne.surf.chat.bukkit.util.MultiPlayerSelectorData
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import org.bukkit.Bukkit

class MultiOfflinePlayerArgument(nodeName: String) : CustomArgument<MultiPlayerSelectorData, String>(StringArgument(nodeName), { info ->
        when(val input = info.input()) {
            "all" -> {
                MultiPlayerSelectorData(true, null)
            }

            else -> {
                MultiPlayerSelectorData(false, Bukkit.getOfflinePlayerIfCached(input) ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Der Spieler $input wurde nicht gefunden.")))
            }
        }
    }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.stringCollection {
            val list = ObjectArraySet<String>()
            list.add("all")
            list.addAll(Bukkit.getOnlinePlayers().map { it.name })
            list
        })
    }
}

inline fun CommandAPICommand.multiOfflinePlayerArgument(nodeName: String, optional: Boolean = false, block: Argument<*>.() -> Unit = {}): CommandAPICommand = withArguments(MultiOfflinePlayerArgument(nodeName).setOptional(optional).apply(block))
