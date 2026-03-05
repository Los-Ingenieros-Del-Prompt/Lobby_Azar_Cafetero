package com.aguardientes.azarcafetero.lobby.infrastructure.web;

import com.aguardientes.azarcafetero.lobby.domain.model.BuildingStructureDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetBuildingStructureUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/building")
public class BuildingController {

    private final GetBuildingStructureUseCase getBuildingStructureUseCase;

    public BuildingController(GetBuildingStructureUseCase getBuildingStructureUseCase) {
        this.getBuildingStructureUseCase = getBuildingStructureUseCase;
    }

    @GetMapping("/structure")
    public ResponseEntity<BuildingStructureDTO> getBuildingStructure() {
        BuildingStructureDTO structure = getBuildingStructureUseCase.execute();
        return ResponseEntity.ok(structure);
    }
}
