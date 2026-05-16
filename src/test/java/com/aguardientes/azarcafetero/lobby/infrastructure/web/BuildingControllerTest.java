package com.aguardientes.azarcafetero.lobby.infrastructure.web;

import com.aguardientes.azarcafetero.lobby.domain.model.BuildingStructureDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetBuildingStructureUseCase;
import com.aguardientes.azarcafetero.lobby.domain.port.in.GetFloorTablesUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingController unit tests")
class BuildingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GetBuildingStructureUseCase getBuildingStructureUseCase;

    @Mock
    private GetFloorTablesUseCase getFloorTablesUseCase;

    @InjectMocks
    private BuildingController buildingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(buildingController).build();
    }

    @Test
    @DisplayName("GET /api/building/structure — Should return structure")
    void getStructure_shouldReturnStructure() throws Exception {
        BuildingStructureDTO structure = new BuildingStructureDTO(List.of());
        when(getBuildingStructureUseCase.execute()).thenReturn(structure);

        mockMvc.perform(get("/api/building/structure"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.floors").isArray());
    }

    @Test
    @DisplayName("GET /api/building/layout — Should return layout")
    void getLayout_shouldReturnLayout() throws Exception {
        BuildingStructureDTO structure = new BuildingStructureDTO(List.of());
        when(getBuildingStructureUseCase.execute()).thenReturn(structure);

        mockMvc.perform(get("/api/building/layout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/building/floors/{id}/tables — Should return tables")
    void getTables_shouldReturnTables() throws Exception {
        UUID floorId = UUID.randomUUID();
        when(getFloorTablesUseCase.execute(any(UUID.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/building/floors/" + floorId + "/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
