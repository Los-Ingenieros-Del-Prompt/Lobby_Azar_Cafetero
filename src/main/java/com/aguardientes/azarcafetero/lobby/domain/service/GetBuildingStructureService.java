package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Building;
import com.aguardientes.azarcafetero.lobby.domain.model.BuildingStructureDTO;
import com.aguardientes.azarcafetero.lobby.domain.model.Floor;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetBuildingStructureUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BuildingRepository;

import java.util.UUID;

public class GetBuildingStructureService implements GetBuildingStructureUseCase {

    private static final UUID DEFAULT_BUILDING_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final BuildingRepository buildingRepository;

    public GetBuildingStructureService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Override
    public BuildingStructureDTO execute() {
        Building building = buildingRepository.findById(DEFAULT_BUILDING_ID)
            .orElseGet(() -> createDefaultBuilding());

        return BuildingStructureDTO.from(building);
    }

    private Building createDefaultBuilding() {
        Floor floor1 = Floor.create(
            UUID.randomUUID(),
            "Piso 1 (Parques)",
            "🎲",
            "/parques"
        );

        Floor floor2 = Floor.create(
            UUID.randomUUID(),
            "Piso 2 (Brisca)",
            "🃏",
            "/brisca"
        );

        Building building = Building.create(DEFAULT_BUILDING_ID);
        building.addFloor(floor1);
        building.addFloor(floor2);

        buildingRepository.save(building);

        return building;
    }
}
