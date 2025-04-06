package dev.slne.surf.chat.api.model

import net.kyori.adventure.text.Component
import java.util.UUID

interface HistoryEntryModel {
   val id: UUID
   val uuid: UUID
   val type: String

   val timestamp: Long
   val message: Component
}