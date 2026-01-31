package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.functionality.Functionalities
import dev.slne.surf.chat.core.service.FunctionalityService
import dev.slne.surf.chat.fallback.repository.functionality.FunctionalityRepository
import net.kyori.adventure.util.Services

@AutoService(FunctionalityService::class)
class FunctionalityServiceImpl : FunctionalityService, Services.Fallback {
    var functionalities = Functionalities.EMPTY

    override suspend fun fetch(localServer: String) {
        val fetched = FunctionalityRepository.findByServerOrCreate(localServer)
        functionalities = fetched
    }

    override fun getFunctionalities(): Functionalities {
        return functionalities
    }

    override suspend fun updateFunctionalities(
        functionalities: Functionalities,
        localServer: String
    ) {
        FunctionalityRepository.updateOrCreate(localServer, functionalities)
    }

    override suspend fun getFunctionalities(localServer: String): Functionalities {
        return FunctionalityRepository.findByServerOrCreate(localServer)
    }

    override suspend fun getFunctionalitiesForAllServers(): Map<String, Functionalities> {
        return FunctionalityRepository.findAll()
    }
}