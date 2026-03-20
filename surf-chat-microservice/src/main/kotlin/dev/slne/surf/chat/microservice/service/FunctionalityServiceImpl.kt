package dev.slne.surf.chat.microservice.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.core.functionality.Functionalities
import dev.slne.surf.chat.microservice.repository.functionality.FunctionalityRepository
import dev.slne.surf.core.api.common.surfCoreApi
import net.kyori.adventure.util.Services

@AutoService(FunctionalityService::class)
class FunctionalityServiceImpl : FunctionalityService, Services.Fallback {
    private var functionalities = Functionalities.EMPTY

    override suspend fun fetch(localServer: String) {
        val fetched = FunctionalityRepository.findByServerOrCreate(localServer)
        functionalities = fetched
    }

    override fun getFunctionalities(): Functionalities {
        return functionalities
    }

    override suspend fun updateLocalFunctionalities(functionalities: Functionalities) {
        updateFunctionalities(functionalities, surfCoreApi.getCurrentServerName())
    }

    override suspend fun updateFunctionalities(
        functionalities: Functionalities,
        localServer: String
    ) {
        FunctionalityRepository.updateOrCreate(localServer, functionalities)
        if (localServer == surfCoreApi.getCurrentServerName()) {
            this.functionalities = functionalities
        }
    }

    override suspend fun getFunctionalities(localServer: String): Functionalities {
        return FunctionalityRepository.findByServerOrCreate(localServer)
    }

    override suspend fun getFunctionalitiesForAllServers(): Map<String, Functionalities> {
        return FunctionalityRepository.findAll()
    }
}