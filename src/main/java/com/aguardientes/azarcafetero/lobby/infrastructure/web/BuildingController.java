package com.aguardientes.azarcafetero.lobby.infrastructure.web;

import com.aguardientes.azarcafetero.lobby.domain.model.BuildingStructureDTO;
import com.aguardientes.azarcafetero.lobby.domain.model.FloorDTO;
import com.aguardientes.azarcafetero.lobby.domain.model.TableDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetBuildingStructureUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetFloorTablesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/building")
public class BuildingController {

    private final GetBuildingStructureUseCase getBuildingStructureUseCase;
    private final GetFloorTablesUseCase getFloorTablesUseCase;

    public BuildingController(GetBuildingStructureUseCase getBuildingStructureUseCase,
                              GetFloorTablesUseCase getFloorTablesUseCase) {
        this.getBuildingStructureUseCase = getBuildingStructureUseCase;
        this.getFloorTablesUseCase = getFloorTablesUseCase;
    }

    @GetMapping("/structure")
    public ResponseEntity<BuildingStructureDTO> getBuildingStructure() {
        BuildingStructureDTO structure = getBuildingStructureUseCase.execute();
        return ResponseEntity.ok(structure);
    }

    @GetMapping("/layout")
    public ResponseEntity<?> getBuildingFloors() {
        try {
            List<FloorDTO> floors = getBuildingStructureUseCase.execute().getFloors();
            return ResponseEntity.ok(floors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    @GetMapping("/pisos/{pisoId}/mesas")
    public ResponseEntity<List<TableDTO>> getFloorTables(@PathVariable UUID pisoId) {
        List<TableDTO> tables = getFloorTablesUseCase.execute(pisoId);
        return ResponseEntity.ok(tables);
    }

}

