package dev.slne.surf.chat.core.paper.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.functionality.Functionalities
import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.FindAllFunctionalitiesRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.FindFunctionalityRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.functionality.UpsertFunctionalityRequestPacket
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.core.paper.rabbiApi
import dev.slne.surf.core.api.common.surfCoreApi
import net.kyori.adventure.util.Services

@AutoService(FunctionalityService::class)
class FunctionalityServiceImpl : FunctionalityService, Services.Fallback {
    private var functionalities = Functionalities.Companion.EMPTY

    override suspend fun fetch(localServer: String) {
        val fetched = rabbiApi.sendRequest(FindFunctionalityRequestPacket(localServer))
        functionalities = fetched.functionalities
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
        rabbiApi.sendRequest(UpsertFunctionalityRequestPacket(localServer, functionalities))
        if (localServer == surfCoreApi.getCurrentServerName()) {
            this.functionalities = functionalities
        }
    }

    override suspend fun getFunctionalities(localServer: String): Functionalities {
        return rabbiApi.sendRequest(FindFunctionalityRequestPacket(localServer)).functionalities
    }

    override suspend fun getFunctionalitiesForAllServers(): Map<String, Functionalities> {
        return rabbiApi.sendRequest(FindAllFunctionalitiesRequestPacket).functionalities
    }
}