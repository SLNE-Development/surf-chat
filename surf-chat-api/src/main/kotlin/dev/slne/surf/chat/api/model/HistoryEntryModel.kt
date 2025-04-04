package dev.slne.surf.chat.api.model

import net.kyori.adventure.text.Component
import java.util.UUID

interface HistoryEntryModel {
   val id: UUID
   val user: UUID
   val userName: String

   val message: Component
   val timestamp: Long
}