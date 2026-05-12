package dev.slne.surf.chat.paper.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class ConnectionMessageConfig(
    @field:Comment("Whether join and quit messages are shown in chat at all.")
    val enabled: Boolean = true,

    @field:Comment(
        "Automatically suppresses join and quit messages for regular players\n" +
        "when too many connection events occur within one minute.\n" +
        "Players with the permission 'surf.chat.connection.always-show'\n" +
        "are exempt and their messages are always displayed."
    )
    val autoDisableOnHighPlayerJoinThreshold: Boolean = true,

    @field:Comment(
        "Maximum number of combined join and quit events per minute before\n" +
        "connection messages are suppressed automatically.\n" +
        "Only relevant when 'autoDisableOnHighPlayerJoinThreshold' is true."
    )
    val joinsPerMinuteThreshold: Int = 15,
)
