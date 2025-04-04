package dev.slne.surf.social.chat.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.mapOfflinePlayerNamesTo
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

@OptIn(DelicateCoroutinesApi::class)
class ChannelMembersArgument(nodeName: String) : CustomArgument<OfflinePlayer, String>(StringArgument(nodeName), { info ->
            val player = Bukkit.getOfflinePlayer(info.input())
            val uniqueId = player.uniqueId
            val channel = channelService.getChannel() ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Du bist in keinem Kanal, oder dieser ist invalid."))

            if (!channel.isMember(uniqueId) && !channel.isOwner(uniqueId)
                && !channel.isModerator(uniqueId)
            ) {
                throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Player is not a member of the channel: ").appendArgInput())
            }

            player
        }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.stringCollectionAsync { info ->
            GlobalScope.future(Dispatchers.IO) {
                val channel =
                    Channel.getChannel(info.sender()) ?: return@future emptyObjectSet()
                val owner = channel.owner ?: return@future emptyObjectSet()

                mutableObjectSetOf<String>().apply {
                    channel.members.mapOfflinePlayerNamesTo(this)
                    channel.moderators.mapOfflinePlayerNamesTo(this)
                    Bukkit.getOfflinePlayer(owner).name?.let { add(it) }
                    remove(info.sender().name)
                }
            }
        })
    }
}