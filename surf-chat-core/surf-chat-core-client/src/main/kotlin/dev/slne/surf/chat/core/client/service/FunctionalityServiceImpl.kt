package dev.slne.surf.chat.core.client.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.functionality.Functionalities
import dev.slne.surf.chat.core.client.rabbitApi
import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.FindAllFunctionalitiesRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.FindFunctionalityRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.UpsertFunctionalityRequestPacket
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.core.api.common.SurfCoreApi
import net.kyori.adventure.util.Services

@AutoService(FunctionalityService::class)
class FunctionalityServiceImpl : FunctionalityService, Services.Fallback {
    private var functionalities = Functionalities.EMPTY

    override suspend fun fetch(localServer: String) {
        val fetched = rabbitApi.sendRequest(FindFunctionalityRequestPacket(localServer)).functionalities
        functionalities = fetched
    }

    override fun getFunctionalities(): Functionalities {
        return functionalities
    }

    override suspend fun updateLocalFunctionalities(functionalities: Functionalities) {
        updateFunctionalities(functionalities, SurfCoreApi.getCurrentServerName())
    }

    override suspend fun updateFunctionalities(
        functionalities: Functionalities,
        localServer: String
    ) {
        rabbitApi.sendRequest(UpsertFunctionalityRequestPacket(localServer, functionalities))
        if (localServer == SurfCoreApi.getCurrentServerName()) {
            this.functionalities = functionalities
        }
    }

    override suspend fun getFunctionalities(localServer: String): Functionalities {
        return rabbitApi.sendRequest(FindFunctionalityRequestPacket(localServer)).functionalities
    }

    override suspend fun getFunctionalitiesForAllServers(): Map<String, Functionalities> {
        return rabbitApi.sendRequest(FindAllFunctionalitiesRequestPacket()).functionalities
    }
}
