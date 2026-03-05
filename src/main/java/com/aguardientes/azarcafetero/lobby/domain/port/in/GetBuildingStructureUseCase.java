package com.aguardientes.azarcafetero.lobby.domain.port.in;

import com.aguardientes.azarcafetero.lobby.domain.model.BuildingStructureDTO;

public interface GetBuildingStructureUseCase {

    BuildingStructureDTO execute();
}
