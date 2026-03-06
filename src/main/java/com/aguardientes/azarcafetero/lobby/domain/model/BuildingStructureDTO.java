package com.aguardientes.azarcafetero.lobby.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BuildingStructureDTO {

    private final List<FloorDTO> floors;

    public BuildingStructureDTO(List<FloorDTO> floors) {
        this.floors = floors;
    }

    public static BuildingStructureDTO from(Building building) {
        List<FloorDTO> floorDTOs = building.getFloors().stream()
            .map(FloorDTO::from)
            .collect(Collectors.toList());
        return new BuildingStructureDTO(floorDTOs);
    }

    public List<FloorDTO> getFloors() {
        return Collections.unmodifiableList(floors);
    }
}
