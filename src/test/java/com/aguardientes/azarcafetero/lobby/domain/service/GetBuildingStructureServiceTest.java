package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Building;
import com.aguardientes.azarcafetero.lobby.domain.model.BuildingStructureDTO;
import com.aguardientes.azarcafetero.lobby.domain.model.Floor;
import com.aguardientes.azarcafetero.lobby.domain.model.FloorDTO;
import com.aguardientes.azarcafetero.lobby.domain.port.out.BuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetBuildingStructureService — UseCase unit tests")
class GetBuildingStructureServiceTest {

    private static final UUID DEFAULT_BUILDING_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private BuildingRepository buildingRepository;

    private GetBuildingStructureService service;

    @BeforeEach
    void setUp() {
        service = new GetBuildingStructureService(buildingRepository);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Happy path: building already exists in the repository
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Returns existing building when found in repository")
    void execute_whenBuildingExists_returnsExistingBuilding() {
        Floor floor = Floor.create(UUID.randomUUID(), "Piso 1 (Parques)", "🎲", "/parques");
        Building existing = new Building(DEFAULT_BUILDING_ID, List.of(floor));
        when(buildingRepository.findById(DEFAULT_BUILDING_ID)).thenReturn(Optional.of(existing));

        BuildingStructureDTO result = service.execute();

        assertThat(result).isNotNull();
        assertThat(result.getFloors()).hasSize(1);
        assertThat(result.getFloors().get(0).getName()).isEqualTo("Piso 1 (Parques)");
        verify(buildingRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Default building creation: repository returns empty
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Creates and saves default building when not found in repository")
    void execute_whenBuildingNotFound_createsDefaultBuilding() {
        when(buildingRepository.findById(DEFAULT_BUILDING_ID)).thenReturn(Optional.empty());

        service.execute();

        verify(buildingRepository, times(1)).save(any(Building.class));
    }

    @Test
    @DisplayName("Default building contains exactly 2 floors")
    void execute_defaultBuilding_hasTwoFloors() {
        when(buildingRepository.findById(DEFAULT_BUILDING_ID)).thenReturn(Optional.empty());

        BuildingStructureDTO result = service.execute();

        assertThat(result.getFloors()).hasSize(2);
    }

    @Test
    @DisplayName("Piso 1 name contains 'Parques' and route leads to /parques")
    void execute_defaultBuilding_floorOneIsParques() {
        when(buildingRepository.findById(DEFAULT_BUILDING_ID)).thenReturn(Optional.empty());

        List<FloorDTO> floors = service.execute().getFloors();
        FloorDTO piso1 = floors.get(0);

        assertThat(piso1.getName()).containsIgnoringCase("parques");
        assertThat(piso1.getRoute()).isEqualTo("/parques");
    }

    @Test
    @DisplayName("Piso 2 name contains 'Brisca' and route leads to /brisca")
    void execute_defaultBuilding_floorTwoIsBrisca() {
        when(buildingRepository.findById(DEFAULT_BUILDING_ID)).thenReturn(Optional.empty());

        List<FloorDTO> floors = service.execute().getFloors();
        FloorDTO piso2 = floors.get(1);

        assertThat(piso2.getName()).containsIgnoringCase("brisca");
        assertThat(piso2.getRoute()).isEqualTo("/brisca");
    }

    @Test
    @DisplayName("Saved building uses the canonical DEFAULT_BUILDING_ID")
    void execute_defaultBuilding_savedWithCorrectId() {
        when(buildingRepository.findById(DEFAULT_BUILDING_ID)).thenReturn(Optional.empty());
        ArgumentCaptor<Building> captor = ArgumentCaptor.forClass(Building.class);

        service.execute();

        verify(buildingRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(DEFAULT_BUILDING_ID);
    }

    @Test
    @DisplayName("Each floor has a non-null, non-blank icon")
    void execute_defaultBuilding_floorsHaveIcons() {
        when(buildingRepository.findById(DEFAULT_BUILDING_ID)).thenReturn(Optional.empty());

        List<FloorDTO> floors = service.execute().getFloors();

        assertThat(floors).allSatisfy(floor ->
                assertThat(floor.getIcon()).isNotBlank());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Performance criterion: use case must respond within 2 seconds
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("UseCase responds in under 2 seconds (PBI performance criterion)")
    void execute_respondsWithinTwoSeconds() {
        when(buildingRepository.findById(DEFAULT_BUILDING_ID)).thenReturn(Optional.empty());

        long start = System.currentTimeMillis();
        service.execute();
        long elapsed = System.currentTimeMillis() - start;

        assertThat(elapsed)
                .as("UseCase must complete in < 2000 ms, but took %d ms", elapsed)
                .isLessThan(2000L);
    }
}
